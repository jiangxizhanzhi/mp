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
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private CsceneInstanceService modelService;


    @Scheduled(cron = "0 */10 * * * ?")
    private void generateEntityLogfile() {

        log.info("start generateEntityLogfile task");

        // 获取运行中的场景列表
        List<CsceneInstanceModel> list;
        try {
            HumanCondition condition = new HumanCondition()
                    .filterEqual(CsceneInstanceModel::getDeleted, false);
            list = csceneInstanceMapper.list(condition);
        }catch (Exception e){
            log.error("list csceneInstance error", e);
            return;
        }
        if (CollectionUtils.isEmpty(list)){
            return;
        }

        // 对场景实例进行实体靶标日志文件记录生成
        for (CsceneInstanceModel csceneInstanceModel : list){
            try {
                generateEntityLogfile4CsceneInstance(csceneInstanceModel);
            }catch (Exception e){
                log.error("GenerateEntityLogfileJob error, csceneInstanceModel={}", csceneInstanceModel, e);
            }
        }
    }

    // 对场景实例进行实体靶标日志文件记录生成
    private void generateEntityLogfile4CsceneInstance(CsceneInstanceModel csceneInstanceModel) {

        // 判断场景实例是否处于进行中状态
        if (!csceneCommonService.isCsceneInstanceRunning(csceneInstanceModel.getId())){
            return;
        }

        // 获取场景实例信息
        ServiceResult<SceneDataBO> serviceResult = sceneService.get(csceneInstanceModel.getScene(), false, null, null);
        Map<String, Map<String, Object>> elements = serviceResult.getSource().getPlugins().get("cpcloud").getElements();
        for (var entry : elements.entrySet()) {
            CloudPluginSceneConfigElementBO elementBO = JsonCommon.snakeMapToBean(entry.getValue(), CloudPluginSceneConfigElementBO.class);
            // 判断是否存在实体靶标
            if (BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(elementBO.getImageType())){
                // 获取设备信息
                DeviceTargetPO deviceTargetPO = deviceTargetMapper.findByUuid(elementBO.getImageUuid());

                if (null == deviceTargetPO) {
                    continue;
                }
                // 判断设备信息是否配置
                if (StringUtils.isNotBlank(deviceTargetPO.getSshConfig()) || StringUtils.isNotBlank(deviceTargetPO.getTelnetConfig())){
                    // 配置信息
                    log.info("deviceTargetPO.getSshConfig={}", deviceTargetPO.getSshConfig());
                    log.info("deviceTargetPO.getTelnetConfig={}", deviceTargetPO.getTelnetConfig());
                    EntityLogfileConfig sshConfig = JSONObject.parseObject(deviceTargetPO.getSshConfig(), EntityLogfileConfig.class);
                    EntityLogfileConfig telnetConfig = JSONObject.parseObject(deviceTargetPO.getTelnetConfig(), EntityLogfileConfig.class);
                    if (checkConfigComplete(sshConfig)){
                        queryEntityLogfile(csceneInstanceModel.getId(), entry.getKey(), TYPE_SSH, elementBO, telnetConfig);
                    }
                    if (checkConfigComplete(telnetConfig)){
                        queryEntityLogfile(csceneInstanceModel.getId(), entry.getKey(),TYPE_TELNET, elementBO, telnetConfig);
                    }
                }
            }
        }
    }

    private void queryEntityLogfile(String csceneId, String nodeId, String type, CloudPluginSceneConfigElementBO elementBO, EntityLogfileConfig config) {
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
            if (TYPE_SSH.equals(type)){
                String url = "http://{ip}:8001/agent/ssh/exec_list_cmds".replace("{ip}", frameServerConfigIp);
                cmdListRequest.setCmds(List.of("ls -l " + config.getFile()));
                result = HttpUtils.sendPost(url, JsonCommon.snakeStringify(cmdListRequest));
            }else if (TYPE_TELNET.equals(type)){
                String url = "http://{ip}:8001/agent/telnet/exec_list_cmds".replace("{ip}", frameServerConfigIp);
                cmdListRequest.setCmds(List.of("ls -l " + config.getFile()));
                result = HttpUtils.sendPost(url, JsonCommon.snakeStringify(cmdListRequest));
            }

            if (StringUtils.isNotBlank(result)){
                // 执行结果解析
                ExecCommandResult execCommandResult = JSONObject.parseObject(result, ExecCommandResult.class);
                // 遍历返回信息
                for (String commandResult : execCommandResult.getData()){
                    if (commandResult.contains(config.getFile()) && !commandResult.contains("ls -l")
                            && !commandResult.contains("No such file or directory")){
                        log.info("queryEntityLogfile result========{}", commandResult);
                        // 示例：-rw-r--r-- 1 root root 56 Nov 30 03:16 /home/123.txt
                        String sizeMessage = commandResult.split("\\s+", -1)[4];
                        log.info("queryEntityLogfile size========{}", sizeMessage);
                        // todo 解析result后将获取的信息记录插入es中
                    }
                }

            }
        }catch (Exception e){
            // 异常不进行抛出，记录异常信息即可
            log.error("queryEntityLogfile error, type={}, elementBO={}, config={}", type, elementBO, config, e);
        }
    }

    private boolean checkConfigComplete(EntityLogfileConfig sshConfig) {
        return null != sshConfig && StringUtils.isNotBlank(sshConfig.getFile())
                && StringUtils.isNotBlank(sshConfig.getIp())
                && StringUtils.isNotBlank(sshConfig.getPassword())
                && StringUtils.isNotBlank(sshConfig.getPort())
                && StringUtils.isNotBlank(sshConfig.getUsername());
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
        private SceneHandler sceneHandler ;
    }

    @Data
    private static class ExecCommandResult {
        List<String> data;
    }
}
