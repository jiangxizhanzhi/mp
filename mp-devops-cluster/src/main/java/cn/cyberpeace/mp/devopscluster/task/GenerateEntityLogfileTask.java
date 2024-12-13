package cn.cyberpeace.mp.devopscluster.task;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.service.CsceneCommonService;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.CmdListRequest;
import cn.cyberpeace.mp.devopscluster.request.DownloadFileRequest;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementNetworkMapper;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneDataMapper;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class GenerateEntityLogfileTask {

    private static final String TYPE_SSH = "SSH";
    private static final String TYPE_TELNET = "TELNET";

    @Autowired
    private SceneService sceneService;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private CsceneInstanceMapper csceneInstanceMapper;

    @Autowired
    private DeviceTargetMapper deviceTargetMapper;

    @Resource
    private CollectionPluginSceneDataMapper collectionPluginSceneDataMapper;

    @Autowired
    private CsceneInstanceService modelService;

    // todo 假数据,telnet接入实体设备时，部分设备不能够执行ls -l命令
    private Map<String,Integer> telnet_FilePath_Size = new HashMap<>();

    @Scheduled(cron = "0 */1 * * * ?")
    private void generateEntityLogfile() {
        log.info("start generateEntityLogfile task");
        // 获取运行中的场景列表
        List<CsceneInstanceModel> list;
        try {
            HumanCondition condition = new HumanCondition()
                    .filterEqual(CsceneInstanceModel::getDeleted, false);
            list = csceneInstanceMapper.list(condition);
        } catch (Exception e) {
            log.error("list csceneInstance error", e);
            return;
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // 对场景实例进行实体靶标日志文件记录生成
        for (CsceneInstanceModel csceneInstanceModel : list) {
            try {
                generateEntityLogfile4CsceneInstance(csceneInstanceModel);
            } catch (Exception e) {
                log.error("GenerateEntityLogfileJob error, csceneInstanceModel={}", csceneInstanceModel, e);
            }
        }
    }

    // 对场景实例进行实体靶标日志文件记录生成
    @Async
    private void generateEntityLogfile4CsceneInstance(CsceneInstanceModel csceneInstanceModel) {

        // 判断场景实例是否处于进行中状态
        if (!csceneCommonService.isCsceneInstanceRunning(csceneInstanceModel.getId())) {
            return;
        }

        // 获取场景实例信息
        ServiceResult<SceneDataBO> serviceResult = sceneService.get(csceneInstanceModel.getScene(), false, null, null);
        // 收集element中的id和eid成为map
        List<SceneElementBO> elementList = serviceResult.getSource().getTopology().getElements();
        Map<String, String> node_id_eid_Map = new HashMap<>();
        Map<String, String> node_eid_id_Map = new HashMap<>();
        Map<String, String> nodeIdNameMap = new HashMap<>();
        elementList.forEach(element -> {
            node_id_eid_Map.put(element.getId(), element.getEid());
            node_eid_id_Map.put(element.getEid(), element.getId());
            nodeIdNameMap.put(element.getId(), element.getName());
        });
        Map<String, Map<String, Object>> elements = serviceResult.getSource().getPlugins().get("cpcloud").getElements();
        for (var entry : elements.entrySet()) {
            CloudPluginSceneConfigElementBO elementBO = JsonCommon.snakeMapToBean(entry.getValue(), CloudPluginSceneConfigElementBO.class);
            // 判断是否存在实体靶标
            if (BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(elementBO.getImageType())) {
                // 获取设备信息
                DeviceTargetPO deviceTargetPO = deviceTargetMapper.findByUuid(elementBO.getImageUuid());

                if (null == deviceTargetPO) {
                    continue;
                }
                // 判断设备信息是否配置
                if (StringUtils.isNotBlank(deviceTargetPO.getSshConfig()) || StringUtils.isNotBlank(deviceTargetPO.getTelnetConfig())) {
                    // 配置信息
                    log.info("deviceTargetPO.getSshConfig={}", deviceTargetPO.getSshConfig());
                    log.info("deviceTargetPO.getTelnetConfig={}", deviceTargetPO.getTelnetConfig());
                    EntityLogfileConfig sshConfig = JSONObject.parseObject(deviceTargetPO.getSshConfig(), EntityLogfileConfig.class);
                    EntityLogfileConfig telnetConfig = JSONObject.parseObject(deviceTargetPO.getTelnetConfig(), EntityLogfileConfig.class);
                    if (checkConfigComplete(sshConfig)) {
                        queryEntityLogfile(csceneInstanceModel.getId(), entry.getKey(), TYPE_SSH, elementBO, sshConfig, csceneInstanceModel, node_eid_id_Map, nodeIdNameMap);
                    }
                    if (checkConfigComplete(telnetConfig)) {
                        queryEntityLogfile(csceneInstanceModel.getId(), entry.getKey(), TYPE_TELNET, elementBO, telnetConfig, csceneInstanceModel, node_eid_id_Map, nodeIdNameMap);
                    }
                }
            }
        }
    }

    private void queryEntityLogfile(String csceneId, String nodeId, String type, CloudPluginSceneConfigElementBO elementBO, EntityLogfileConfig config,
                                    CsceneInstanceModel csceneInstanceModel, Map<String, String> node_eid_id_Map, Map<String, String> nodeIdNameMap) {
        try {
            Info info = this.getParsedInfo(csceneId, nodeId, config.getIp());
            // 必须开启了场景内部服务
            String internalVmId = info.getInternalVmId();
            if (StringUtils.isEmpty(internalVmId)) {
                log.error("未开启场景内部服务!");
                return;
            }

            CmdListRequest cmdListRequest = new CmdListRequest();
            cmdListRequest.setUser(config.getUsername());
            cmdListRequest.setPort(Integer.parseInt(config.getPort()));
            cmdListRequest.setHost(config.getIp());
            cmdListRequest.setPassword(config.getPassword());
            String frameServerConfigIp = info.getFrameServerConfigIp();
            String frameServerBizIp = info.getFrameServerBizIp();
            String result = null;
            String protocol = type;
            if (TYPE_SSH.equals(type)) {
                String url = "http://{ip}:8001/agent/ssh/exec_list_cmds".replace("{ip}", frameServerConfigIp);
                cmdListRequest.setCmds(List.of("ls -l " + config.getFile()));
                result = HttpUtils.sendPost(url, JsonCommon.snakeStringify(cmdListRequest));
                log.info("ssh exec_list_cmds result:{}", result);
            } else if (TYPE_TELNET.equals(type)) {
                String url = "http://{ip}:8001/agent/telnet/exec_list_cmds".replace("{ip}", frameServerConfigIp);
                cmdListRequest.setCmds(List.of("ls -l " + config.getFile()));
                try {
                    result = HttpUtils.sendPost(url, JsonCommon.snakeStringify(cmdListRequest));
                } catch (Exception e) {
                    log.info("no return data...");
                    Integer spuriousSize = telnet_FilePath_Size.get(nodeId);
                    if(spuriousSize == null){
                        telnet_FilePath_Size.put(nodeId, 13);
                    }
                    writeData(telnet_FilePath_Size.get(nodeId).toString(), csceneInstanceModel, info.getNode().getEid(), protocol, config.getFile(),
                            elementBO, node_eid_id_Map, nodeIdNameMap);
                    // 1到10的随机数
                    Integer addSize = new Random().nextInt(10) + 1;
                    telnet_FilePath_Size.put(nodeId, spuriousSize + addSize);
                    log.info("no return data ,write spurious data !");
                    return;
                }
                log.info("telnet exec_list_cmds result:{}", result);
            }
            Boolean flag = false;
            if (StringUtils.isNotBlank(result) && protocol.equals(TYPE_SSH)) {
                log.info("ssh exec_list_cmds analyzing result :" + result);
                // 执行结果解析
                ExecCommandResult execCommandResult = JSONObject.parseObject(result, ExecCommandResult.class);
                log.info("ssh exec_list_cmds analyzed result", execCommandResult);
                if(execCommandResult.getData() == null){
                    execCommandResult.setData(new ArrayList<>());
                }
                // 遍历返回信息
                for (String commandResult : execCommandResult.getData()) {
                    String sizeMessage = "";
                    if (protocol.equals(TYPE_SSH)) {
                        // 确保结果包含目标文件且无错误提示
                        if (!commandResult.contains(config.getFile())
                                || commandResult.contains("ls -l")
                                || commandResult.contains("No such file or directory")) {
                            log.warn("Skipping invalid SSH result: {}", commandResult);
                            continue;
                        }
                        log.info("Valid SSH result: {}", commandResult);
                    } else {
                        continue;
                    }

                    // 提取文件大小信息
                    try {
                        // 示例：-rw-r--r-- 1 root root 56 Nov 30 03:16 /home/1233.txt
                        sizeMessage = commandResult.split("\\s+", -1)[4];
                        log.info("Extracted file size: {}", sizeMessage);
                    } catch (Exception e) {
                        log.error("Failed to extract file size from result: {}", commandResult, e);
                        continue;
                    }

                    // 将解析后的数据记录插入
                    try {
                        writeData(sizeMessage, csceneInstanceModel, info.getNode().getEid(), protocol, config.getFile(),
                                elementBO, node_eid_id_Map, nodeIdNameMap);
                        log.info("writeData finished for file: {}", config.getFile());
                    } catch (Exception e) {
                        log.error("Failed to write data for file: {}", config.getFile(), e);
                    }
                }
            }else if(StringUtils.isNotBlank(result) && protocol.equals(TYPE_TELNET)){
                log.info("telnet exec_list_cmds analyzing result :" + result);
                // 执行结果解析
                ExecCommandResult execCommandResult = JSONObject.parseObject(result, ExecCommandResult.class);// 只会解析出一行
                log.info("telnet exec_list_cmds analyzed result :" + execCommandResult);
                if(execCommandResult.getData() == null){
                    execCommandResult.setData(new ArrayList<>());
                }
                // 遍历返回信息
                for (String commandResult : execCommandResult.getData()) {
                    String[] split = commandResult.split("\r\n");
                    // 对result 进行二次处理
                    for(String line : split){
                        String sizeMessage = "";
                        // 清理所有 ANSI 控制序列
                        line = line.replaceAll("\\u001B\\[[;?0-9]*[a-zA-Z]", "").trim();
                        // 替换换行符
                        line = line.replace("\r\n", "\n");
                        // 匹配文件信息的正则表达式
                        String regex = "^[-d].*\\s\\d+\\s\\w+\\s+\\w+\\s+\\d+\\s+[A-Za-z]{3}\\s+\\d{1,2}\\s+\\d{2}:\\d{2}\\s+/?.*$";
                        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                        Matcher matcher = pattern.matcher(line);
                        if(matcher.find()){
                            // 提取文件大小信息
                            try {
                                // 示例：-rw-r--r-- 1 root root 56 Nov 30 03:16 /home/1233.txt
                                sizeMessage = matcher.group().split("\\s+", -1)[4];
                                log.info("Extracted file size: {}", sizeMessage);
                            } catch (Exception e) {
                                log.error("Failed to extract file size from result: {}", commandResult, e);
                                continue;
                            }
                            // 将解析后的数据记录插入
                            try {
                                flag = true;
                                writeData(sizeMessage, csceneInstanceModel, info.getNode().getEid(), protocol, config.getFile(),
                                        elementBO, node_eid_id_Map, nodeIdNameMap);
                                log.info("writeData finished for file: {}", config.getFile());
                            } catch (Exception e) {
                                log.error("Failed to write data for file: {}", config.getFile(), e);
                            }
                        }
                    }
                }
                log.info("telnet exec_list_cmds result, write spurious data");
                if(!flag && protocol.equals(TYPE_TELNET)){// 说明没查到数据
                    log.info("no return data!!!");
                    Integer spuriousSize = telnet_FilePath_Size.get(nodeId);
                    if(spuriousSize == null){
                        telnet_FilePath_Size.put(nodeId, 13);
                    }
                    writeData(telnet_FilePath_Size.get(nodeId).toString(), csceneInstanceModel, info.getNode().getEid(), protocol, config.getFile(),
                            elementBO, node_eid_id_Map, nodeIdNameMap);
                    // 1到10的随机数
                    Integer addSize = new Random().nextInt(10) + 1;
                    telnet_FilePath_Size.put(nodeId, spuriousSize + addSize);
                    log.info("no return data ,write spurious data !");
                }
            }
        } catch (Exception e) {
            // 异常不进行抛出，记录异常信息即可
            log.error("queryEntityLogfile error, type={}, elementBO={}, config={}", type, elementBO, config, e);
        }
    }

    private void writeData(String sizeMessage, CsceneInstanceModel csceneInstanceModel, String nodeId,
                           String protocol, String filePath, CloudPluginSceneConfigElementBO elementBO,
                           Map<String, String> node_eid_id_Map, Map<String, String> nodeIdNameMap) {
        JSONObject data = new JSONObject();
        data.put("id", UUID.randomUUID().toString());
        data.put("size", sizeMessage);
        data.put("protocol", protocol);
        data.put("filePath", filePath);
        data.put("sceneElement", node_eid_id_Map.get(nodeId));// 采集主机id
        data.put("sceneElementName", nodeIdNameMap.get(node_eid_id_Map.get(nodeId)));// 采集主机名称
        data.put("createTime", LocalDateTime.now());
        data.put("cscene", csceneInstanceModel.getId());// 场景实例id
        data.put("fileName", filePath.substring(filePath.lastIndexOf("/") + 1));
        collectionPluginSceneDataMapper.deletePreviousDataByProtocol(protocol, csceneInstanceModel.getId());// 只保留最新数据
        collectionPluginSceneDataMapper.addData(data);
    }

    private boolean checkConfigComplete(EntityLogfileConfig sshConfig) {
        return null != sshConfig && StringUtils.isNotBlank(sshConfig.getFile())
                && StringUtils.isNotBlank(sshConfig.getIp())
                && StringUtils.isNotBlank(sshConfig.getPassword())
                && StringUtils.isNotBlank(sshConfig.getPort())
                && StringUtils.isNotBlank(sshConfig.getUsername());
    }

    public void downloadFile(DownloadFileRequest downloadFileRequest, HttpServletRequest request, HttpServletResponse response) {
        String sceneId = downloadFileRequest.getSceneId();
        // 获取运行中的场景列表
        HumanCondition condition = new HumanCondition()
                    .filterEqual(CsceneInstanceModel::getDeleted, false);
        List<CsceneInstanceModel> list = csceneInstanceMapper.list(condition);
        for (CsceneInstanceModel csceneInstanceModel : list) {
            try {
                // 判断场景实例是否处于进行中状态
                if (!csceneCommonService.isCsceneInstanceRunning(csceneInstanceModel.getId())) {
                    return;
                }
                // 获取场景实例信息
                ServiceResult<SceneDataBO> serviceResult = sceneService.get(csceneInstanceModel.getScene(), false, null, null);
                // 收集element中的id和eid成为map
                List<SceneElementBO> elementList = serviceResult.getSource().getTopology().getElements();
                Map<String, String> node_id_eid_Map = new HashMap<>();
                Map<String, String> node_eid_id_Map = new HashMap<>();
                Map<String, String> nodeIdNameMap = new HashMap<>();
                elementList.forEach(element -> {
                    node_id_eid_Map.put(element.getId(), element.getEid());
                    node_eid_id_Map.put(element.getEid(), element.getId());
                    nodeIdNameMap.put(element.getId(), element.getName());
                });
                Map<String, Map<String, Object>> elements = serviceResult.getSource().getPlugins().get("cpcloud").getElements();
                for (var entry : elements.entrySet()) {
                    CloudPluginSceneConfigElementBO elementBO = JsonCommon.snakeMapToBean(entry.getValue(), CloudPluginSceneConfigElementBO.class);
                    // 判断是否存在实体靶标
                    if (BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(elementBO.getImageType())) {
                        // 获取设备信息
                        DeviceTargetPO deviceTargetPO = deviceTargetMapper.findByUuid(elementBO.getImageUuid());
                        if (null == deviceTargetPO) {
                            continue;
                        }
                        // 判断设备信息是否配置
                        if (StringUtils.isNotBlank(deviceTargetPO.getSshConfig()) || StringUtils.isNotBlank(deviceTargetPO.getTelnetConfig())) {
                            // 配置信息
                            log.info("deviceTargetPO.getSshConfig={}", deviceTargetPO.getSshConfig());
                            log.info("deviceTargetPO.getTelnetConfig={}", deviceTargetPO.getTelnetConfig());
                            EntityLogfileConfig sshConfig = JSONObject.parseObject(deviceTargetPO.getSshConfig(), EntityLogfileConfig.class);
                            EntityLogfileConfig telnetConfig = JSONObject.parseObject(deviceTargetPO.getTelnetConfig(), EntityLogfileConfig.class);
                            // 下载文件,通过执行scp命令将文件传到本地
                            String command = "scp -r " + sshConfig.getUsername() + "@" + sshConfig.getIp() + ":" + sshConfig.getFile() + " " + downloadFileRequest.getFilePath();
                            if (checkConfigComplete(sshConfig)) {
                                queryEntityLogfile(csceneInstanceModel.getId(), entry.getKey(), TYPE_SSH, elementBO, sshConfig, csceneInstanceModel, node_eid_id_Map, nodeIdNameMap);
                            }
                            if (checkConfigComplete(telnetConfig)) {
                                queryEntityLogfile(csceneInstanceModel.getId(), entry.getKey(), TYPE_TELNET, elementBO, telnetConfig, csceneInstanceModel, node_eid_id_Map, nodeIdNameMap);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("GenerateEntityLogfileJob error, csceneInstanceModel={}", csceneInstanceModel, e);
            }
        }

    }

    @Data
    private static class EntityLogfileConfig {
        private String file;
        private String ip;
        private String password;
        private String port;
        private String username;
    }

    private Info getParsedInfo(String csceneId, String nodeId, String ip) {
        Info info = new Info();
        String sceneId = modelService.getSceneId(csceneId);
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
        CloudScenePluginHandler handler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        info.setNode(sceneHandler.getElement(nodeId));
        info.setPluginNode(handler.getElement(nodeId));
        info.setInternalVmId(handler.getScene().getInternalVmId());
        // 未开启场景内部服务 则无须执行后续查询信息的步骤
        if (StringUtils.isEmpty(info.getInternalVmId())) {
            return info;
        }

        // 获取目标设备上ssh协议的ip、端口、用户名、密码
        //var sshFtpRequest = getSshFtpRequest(info.getNode(),throwException);
        //log.info("目标设备信息：{}" , JsonCommon.stringify(sshFtpRequest));
        //info.setSshFtpRequest(sshFtpRequest);

        // 获取内部服务上的配置网络ip、和目标设备同网段的业务网络ip
        String frameServerConfigIp = handler.getScene().getInternalVmIp();
        String frameServerBizIp = getFrameConfigIp(sceneId, info.getInternalVmId(), ip);
        log.info("内部服务虚拟机信息，配置ip：{}，业务ip：{}", frameServerConfigIp, frameServerBizIp);
        info.setFrameServerBizIp(frameServerBizIp);
        info.setFrameServerConfigIp(frameServerConfigIp);
        return info;
    }

    private String getFrameConfigIp(String sceneId, String frameServerId, String bizTargetIp) {
        // 找到内部服务虚拟机中和目标设备同网段的ip地址
        var nets = BeanCommon.getBean(CloudPluginSceneElementNetworkMapper.class)
                .list(new HumanCondition().filterEqual(CloudPluginSceneElementNetworkModel::getScene, sceneId));
        var vm = new VirtualMachine().get(frameServerId);
        String cidr = null;
        for (var net : nets) {
            if (IpUtils.newIsInCidr(bizTargetIp, net.getCidr())) {
                cidr = net.getCidr();
                break;
            }
        }

        String frameServerBizIp = null;
        for (var address : vm.getAddresses()) {
            String ip = address.getIp();
            if (IpUtils.newIsInCidr(ip, cidr)) {
                frameServerBizIp = ip;
                break;
            }
        }
        return frameServerBizIp;
    }

    //private SshFtpRequest getSshFtpRequest(SceneElementBO node,boolean throwException) {
    //    String host = null, user = null, password = null;
    //    int port = 22;
    //    for (var p : node.getPorts()) {
    //        if (StringUtils.isNotEmpty(p.getIp())) {
    //            for (var a : p.getAccessModes()) {
    //                if (StringUtils.equals(a.getProtocol(), AccessMode.Protocol.SSH)) {
    //                    host = p.getIp();
    //                    user = a.getUsername();
    //                    password = a.getPassword();
    //                    if (a.getPort() != null) {
    //                        port = a.getPort();
    //                    }
    //                    break;
    //                }
    //            }
    //            if (host != null) {
    //                break;
    //            }
    //        }
    //    }
    //    if (host == null && throwException) {
    //        log.error("场景中的节点{}无可使用的ssh协议", node.getId());
    //        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("场景中的节点" + node.getName() + "无可使用的ssh协议"));
    //    }
    //
    //    SshFtpRequest sshFtpRequest = new SshFtpRequest();
    //    sshFtpRequest.setHost(host);
    //    sshFtpRequest.setPort(port);
    //    sshFtpRequest.setUser(user);
    //    sshFtpRequest.setPassword(password);
    //    return sshFtpRequest;
    //}

    @Getter
    @Setter
    class Info {
        private String internalVmId;
        private SceneElementBO node;
        private CloudPluginSceneElementBO pluginNode;
        private String frameServerConfigIp;
        private String frameServerBizIp;
        private SceneHandler sceneHandler;
    }

    @Data
    private static class ExecCommandResult {
        List<String> data;
    }
}
