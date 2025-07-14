package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.bo.AttackType;
import cn.cyberpeace.mp.base.bo.ImageToolRequest;
import cn.cyberpeace.mp.base.bo.OutAttackToolRequest;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.out.FlowInfo;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.repository.OptionMapper;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.request.flow.AddFlowTaskTaskDetail;
import cn.cyberpeace.mp.cscene.request.flow.AddFlowTaskTaskRequest;
import cn.cyberpeace.mp.cscene.request.flow.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.cscene.request.flow.OutStartFlowTaskRequest;
import cn.cyberpeace.mp.cscene.service.CsceneFlowService;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.util.AttackDataOutputUtil;
import cn.cyberpeace.mp.cscene.util.PythonExecUtil;
import cn.cyberpeace.mp.cscene.ws.WebSocketClientService;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.FlowTaskCommonConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskFlowConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskIpv4Conf;
import cn.cyberpeace.mp.flow.bo.FlowTaskRateConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskTcpConf;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.TaskTaskStatusEnum;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskRecordMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 场景流量
 * @Author: guquan
 * @Date：2023/5/8 14:56
 */
@Api(value = "场景流量", tags = "场景流量")
@Slf4j
@RestController
@RequestMapping("/unauth/flow")
@Data
public class CsceneOutController implements BaseModelListControllerBase {

    @Resource
    private CsceneInstanceService csceneInstanceService;

    @Resource
    private CsceneFlowService csceneFlowService;

    @Resource
    private FlowInfoMapper flowInfoMapper;

    @Resource
    private FlowTaskTaskService flowTaskTaskService;

    @Resource
    private AttackDataOutputUtil attackDataOutputUtil;

    @Autowired
    private WebSocketClientService webSocketClientService;

    /**
     * 新增流量任务
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("新增并启动一个流量任务")
    @PostMapping("/start")
    public ServiceResult<String> startTast(@RequestBody OutStartFlowTaskRequest request) {
        // 查找源节点和目标节点的信息
        var sceneId = csceneInstanceService.getSceneId(request.getId());
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, false);
        CloudScenePluginHandler cloudScenePluginHandler = sceneHandler.getPluginHandler("cpcloud");
        var source = sceneHandler.getElement(request.getSourceNode());
        var target = sceneHandler.getElement(request.getTargetNode());
        String sourceIp = source.getPorts().get(0).getIp();
        String targetIp = target.getPorts().get(0).getIp();

        // 测试该流量任务中 源节点和目标节点的联通性
        PintTest pintTest = this.getPingTest(source, target, cloudScenePluginHandler);
        if (!Boolean.TRUE.equals(pintTest.getConnected())) {
            // 如果不联通 返回错误？ todo
            return null;
        } else {
            // 如果联通 则选择流量并发送流量
            // 根据流量类型从流量中选择一个
            var chooseFlow = this.chooseFlow(String.valueOf(request.getType()));

            // 向场景中加入一个流量任务 , 得到一个任务id
            var task = this.addTask(chooseFlow, request.getId(), target.getId());
            String taskId = task.getId();

            // 设定好固定的流量配置
            this.editTaskConfig(taskId, sourceIp, targetIp);

            // 联通性记录到缓存里 ， 给外部推送数据时，同步推送一下
            OutputFlowMessage.pingTestMap.put(taskId, pintTest);

            // 启动流量
            csceneFlowService.startTask(taskId);
            return new ServiceResult(taskId).asResult();
        }
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("流量执行结果")
    @GetMapping("/flowinfo")
    public ServiceResult<FlowInfo> queryFlowInfo(String taskId) {
        FlowTaskTaskMapper taskTaskMapper = BeanCommon.getBean(FlowTaskTaskMapper.class);
        var task = taskTaskMapper.first(new HumanCondition().filterEqual(FlowTaskTaskModel::getId, taskId));
        if (task == null) {
            // 任务不存在
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }
        if (task.getTaskStatus() == TaskTaskStatusEnum.ERROR.toValue()) {
            // 任务执行失败
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }
        if (task.getTaskStatus() != TaskTaskStatusEnum.STOPPED.toValue()) {
            // 任务未结束
            throw new MpRuntimeException(FlowErrorCode.TASK_IS_RUNNING);
        }
        // 取最后一条统计数据
        FlowTaskRecordMapper recordMapper = BeanCommon.getBean(FlowTaskRecordMapper.class);
        var record = recordMapper.first(new HumanCondition().filterEqual(FlowTaskRecordModel::getTaskId, taskId)
                                                .orderBy(FlowTaskRecordModel::getPk, true));
        // 取联通测试结果
        var pintTest = OutputFlowMessage.pingTestMap.get(taskId);
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setDelay(pintTest.getDelay());
        flowInfo.setPacketLoss(pintTest.getPacketLoss());
        flowInfo.setConnected(pintTest.getConnected());
        flowInfo.setTxBps(record.getTxBps());
        flowInfo.setObytes(record.getObytes());
        // 这里可以改成向外部推送数据
        JSONObject jsonObject = new JSONObject();
        JSONObject dataJsonObject = new JSONObject();
        dataJsonObject.put("data", flowInfo);
        jsonObject.put("Data", dataJsonObject.toJSONString());
        jsonObject.put("Function", "UniteFlowResultData");
        jsonObject.put("ReceiveObjectName", "");
        jsonObject.put("SendObjectName", "");
//        发送构建数据
        String jsonString = jsonObject.toJSONString();
        webSocketClientService.sendMessage(jsonString);
        return new ServiceResult<>(flowInfo).asResult();
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("使用ping检测源和目标节点的联通性")
    @PostMapping("/test_ping")
    public ServiceResult<ConnectInfo> pingTest(@RequestBody OutStartFlowTaskRequest request) {
//        1. ojj定时任务五秒执行一次判断已启动的联合训练
//            查所有启动的有任务id的场景
//        2. 拿到场景信息、需要判断的链路信息发送mp请求
//        3. mp处理信息后，包装数据发送320
        // 查找源节点和目标节点的信息
        var sceneId = csceneInstanceService.getSceneId(request.getId());
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, false);
        var source = sceneHandler.getElement(request.getSourceNode());
        var target = sceneHandler.getElement(request.getTargetNode());
        ConnectInfo connectInfo = getConnectInfo(sceneHandler, source, target);
        return new ServiceResult(connectInfo).asResult();
    }

    private ConnectInfo getConnectInfo(SceneHandler sceneHandler, SceneElementBO source, SceneElementBO target) {
        if(source == null || target == null) {
            ConnectInfo connectInfo = new ConnectInfo();
            connectInfo.setConnected(false);
            return connectInfo;
        }
        String sourceIp = "";
        Integer sourcePort = null;
        String targetIp = target.getPorts().get(0).getIp();
        String username = "";
        String password = "";
        CloudScenePluginHandler handler = sceneHandler.getPluginHandler("cpcloud");
        for (var port : source.getPorts()) {
            var pluginPort = handler.getPort(port.getId());
            if (CollectionUtils.isNotEmpty(pluginPort.getAccessModes())) {
                for (var a : pluginPort.getAccessModes()) {
                    if (AccessMode.Protocol.SSH.equals(a.getProtocol())) {
                        username = a.getUsername();
                        password = a.getPassword();
                        sourceIp = a.getAccessIp();
                        sourcePort = a.getAccessPort();
                    }
                }
            }
        }
        String dir = BeanCommon.getBean(BaseAppConfig.class).getResourcesDir();
        String pintTestPath = dir + "/" + "pingtest.py";
        String res = PythonExecUtil.exec(pintTestPath, sourceIp, String.valueOf(sourcePort), targetIp, username, password);
        var pingRes = JsonCommon.snakeParse(res, PintTest.class);
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setConnected(pingRes.getConnected());
        connectInfo.setDelay(pingRes.getDelay());
        connectInfo.setPacketLoss(pingRes.getPacketLoss());
        var hostInfo1 = attackDataOutputUtil.getHostInfo(source.getEid());
        if (hostInfo1 != null) {
            connectInfo.setFlow(hostInfo1.getMaxNetSpeed());
        }
        connectInfo.setObytes(this.getObytes(source.getEid()));
        return connectInfo;
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("使用ping检测源和目标节点的联通性")
    @PostMapping("/test_ping_ping")
    public ServiceResult<Boolean> pingPingTest(@RequestBody JSONArray jsonArray) {
        List<JSONObject> jsonObjects = jsonArray.toJavaList(JSONObject.class);
        for (JSONObject jsonObject : jsonObjects) {
            List<ConnectInfo> connectInfoList = new ArrayList<>();
            String mpSceneId = jsonObject.getString("mpSceneId");
            String taskId = jsonObject.getString("taskId");
            var sceneId = csceneInstanceService.getSceneId(mpSceneId);
            SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, false);
            Map<String, SceneElementBO> uniteElementMap = sceneHandler.getElementMap().values().stream()
                    .collect(Collectors.toMap(element -> element.getUniteNumberId(), element -> element, (a, b) -> a));
            for (JSONObject link : jsonObject.getJSONArray("links").toJavaList(JSONObject.class)) {
                String sourceNumberId = link.getString("source_number_id");
                String targetNumberId = link.getString("target_number_id");
                SceneElementBO source = uniteElementMap.get(sourceNumberId);
                SceneElementBO target = uniteElementMap.get(targetNumberId);
                ConnectInfo connectInfo = getConnectInfo(sceneHandler, source, target);
                connectInfo.setTaskId(taskId);
                connectInfo.setLinkId(link.getString("link_id"));
                connectInfo.setSourceNumberId(sourceNumberId);
                connectInfo.setTargetNumberId(targetNumberId);
                JSONObject resultJson = new JSONObject();
                JSONObject dataJsonObject = new JSONObject();
                dataJsonObject.put("data", connectInfo);
                resultJson.put("Data", dataJsonObject.toJSONString());
                resultJson.put("Function", "UniteLinkStatusData");
                resultJson.put("ReceiveObjectName", "");
                resultJson.put("SendObjectName", "");
//        发送构建数据
                String jsonString = resultJson.toJSONString();
                webSocketClientService.sendMessage(jsonString);
            }
        }
        return new ServiceResult().asResult();
    }

    private Long getObytes(String eid) {
        CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
        collectorBaseRequest.setAgentName(eid);
        collectorBaseRequest.setPage(1);
        collectorBaseRequest.setPageSize(10);
        collectorBaseRequest.setPerPage(10);
        SysCollectorService sysCollectorService = BeanCommon.getBean(SysCollectorService.class);
        List<Netiface> netifaces = new ArrayList<>();
        var x = sysCollectorService.pageNetifaces(collectorBaseRequest);
        netifaces.addAll(x.getSource().getObjs());
        Long obytes = 0L;
        for (Netiface netiface : netifaces) {
            if (netiface.getTx() != null && netiface.getTx().getBytes() != null) {
                obytes += netiface.getTx().getBytes();
            }
        }
        return obytes;
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("启动攻击工具")
    @PostMapping("/attack")
    public void attack(@RequestBody OutAttackToolRequest request) {
//        判断当前时间是否在开始时间内 若在开始时间内直接执行
        var sceneId = csceneInstanceService.getSceneId(request.getId());
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, false);
        CloudScenePluginHandler handler = sceneHandler.getPluginHandler("cpcloud");
        var source = sceneHandler.getElement(request.getSourceNode());
        var target = sceneHandler.getElement(request.getTargetNode());
        var pluginSource = handler.getElement(source.getId());
        var pluginTarget = handler.getElement(target.getId());
        // 取源节点上的ssh信息
        SshInfo sshInfo = this.getSshInfo(source.getPorts(), handler);

        for (ImageToolRequest.Params param : request.getParams()) {
            if("ip".equals(param.getName()) && StringUtils.isBlank(param.getValue())) {
//                ip不需要输入
                String sourceIp = sshInfo.getIp();
                String targetIp = "";
                for (int i = 0; i < target.getPorts().size(); i++) {
                    if(i==0){
                        targetIp = target.getPorts().get(i).getIp();
                    }
                    if(areFirstThreeOctetsSame(sourceIp,target.getPorts().get(i).getIp())){
                        targetIp = target.getPorts().get(i).getIp();
                    }
                }
                param.setValue(targetIp);
            }
        }

        // 取到指定工具
        ImageToolRequest tool = null;
        for (var t : pluginTarget.getAttackTools()) {
            if (StringUtils.equals(t.getId(), request.getToolId())) {
                tool = t;
            }
        }
        tool.setParams(request.getParams());
//        执行前需要先查询数据用于对比 查询次数按开始结束时间差+5分钟进行
        Date attackStarttime = request.getAttackStarttime();
        Date attackEndtime = request.getAttackEndtime();
        Long attackSconds = calculateSecondsBetween(attackStarttime,  attackEndtime) + 300;
        //         启动一个线程 查询攻击结果 每隔5秒查询一次es数据，查最新的一条数据
        CollectionScenePluginHandler collectionScenePluginHandler = sceneHandler.getPluginHandler("cpcollection");
        attackDataOutputUtil.startOneAttackDataOutputTask(collectionScenePluginHandler, source, target, tool, attackSconds, request.getTaskId(), request.getTargetId());

        // 构建执行参数
        String cmd = AttackType.getFormatCmd(tool.getAttackType(), tool.getParams());
        Map<String, Object> p = new HashMap<>();
        p.put("ip", sshInfo.getIp());
        p.put("port", sshInfo.getPort());
        p.put("username", sshInfo.getUsername());
        p.put("password", sshInfo.getPassword());
        p.put("cmd", cmd);
        // 执行参数使用base64编码 然后执行
        var encodeParams = TextUtils.encodeBase64(JsonCommon.stringify(p));
        String dir = BeanCommon.getBean(BaseAppConfig.class).getResourcesDir();
        String pintTestPath = dir + "/" + "ssh_exec.py";
        PythonExecUtil.exec(pintTestPath, encodeParams);
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("查询单次攻击数据")
    @PostMapping("/attack_result")
    public ServiceResult<AttackDataOutputUtil.DataWrapper> attackResult(@RequestBody OutAttackToolRequest request) {
        var sceneId = csceneInstanceService.getSceneId(request.getId());
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, false);
        CloudScenePluginHandler handler = sceneHandler.getPluginHandler("cpcloud");
        var source = sceneHandler.getElement(request.getSourceNode());
        var target = sceneHandler.getElement(request.getTargetNode());
        var pluginTarget = handler.getElement(target.getId());
        CollectionScenePluginHandler collectionScenePluginHandler = sceneHandler.getPluginHandler("cpcollection");
        // 取到指定工具
        ImageToolRequest tool = null;
        for (var t : pluginTarget.getAttackTools()) {
            if (StringUtils.equals(t.getId(), request.getToolId())) {
                tool = t;
            }
        }
        tool.setParams(request.getParams());
        String filePath = "D:\\a.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < 300000/5; i++) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        AttackDataOutputUtil.DataWrapper dataWrapper = attackDataOutputUtil.queryOneAttackData(collectionScenePluginHandler, source, target, tool);
                System.out.println("cpu:"+ dataWrapper.getHostInfo().getCPUPercent()+"内存使用率："+dataWrapper.getHostInfo().getMemoryUsedPercent()+ " time："+ dataWrapper.getHostInfo().getTimestamp());
                bw.write("cpu:"+ dataWrapper.getHostInfo().getCPUPercent()+"内存使用率："+dataWrapper.getHostInfo().getMemoryUsedPercent()+ " time："+ dataWrapper.getHostInfo().getTimestamp());
                bw.newLine();
                // 这里可以改成向外部推送数据
                dataWrapper.setTaskId(request.getTaskId());
                dataWrapper.setToolId(request.getToolId());
                dataWrapper.setTargetId(request.getTargetId());
                JSONObject jsonObject = new JSONObject();
                JSONObject dataJsonObject = new JSONObject();
                dataJsonObject.put("data", dataWrapper);
                jsonObject.put("Data", dataJsonObject.toJSONString());
                jsonObject.put("Function", "AttackResultData");
                jsonObject.put("ReceiveObjectName", "");
                jsonObject.put("SendObjectName", "");
//        发送构建数据
                String jsonString = jsonObject.toJSONString();
                webSocketClientService.sendMessage(jsonString);
                log.info("sendMessage320,taskId:{},toolId:{}",request.getTaskId(),request.getToolId());
            }
            return null;
        }catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private List<FlowTaskTaskModel> addTask(AddFlowTaskTaskRequest request) {
        log.debug("CsceneFlowService.addTask begin. request:{}", request);
        String sceneId = csceneInstanceService.getSceneId(request.getId());
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_STARTED);
        }

        // 过滤未添加流量的节点
        List<AddFlowTaskTaskDetail> list = new ArrayList<>();
        for (AddFlowTaskTaskDetail addFlowTaskTaskDetail : request.getDetails()) {
            if (CollectionUtils.isNotEmpty(addFlowTaskTaskDetail.getFlowInfos())) {
                list.add(addFlowTaskTaskDetail);
            }
        }
        request.setDetails(list);

        List<FlowTaskTaskModel> flowTaskTaskModelList = new ArrayList<FlowTaskTaskModel>();
        for (AddFlowTaskTaskDetail detail : request.getDetails()) {
            HumanCondition condition = new HumanCondition();
            List<String> flowIds = new ArrayList<>();
            for (AddFlowTaskFlowInfo obj : detail.getFlowInfos()) {
                String flowId = obj.getFlowId();
                flowIds.add(flowId);
            }
            Map<String, AddFlowTaskFlowInfo> addFlowTaskFlowInfoMap = new HashMap<>();
            for (AddFlowTaskFlowInfo addFlowTaskFlowInfo : detail.getFlowInfos()) {
                if (addFlowTaskFlowInfoMap.put(addFlowTaskFlowInfo.getFlowId(), addFlowTaskFlowInfo) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            condition.filterIn(FlowInfoModel::getId, flowIds.toArray());
            List<FlowInfoModel> flowInfoModelList = flowInfoMapper.list(condition);
            //保存流量任务信息
            for (FlowInfoModel flowInfoModel : flowInfoModelList) {
                FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
                flowTaskTaskModel.setSceneId(sceneId);
                flowTaskTaskModel.setResourceType(flowInfoModel.getResourceType());
                flowTaskTaskModel.setInteractiveType(flowInfoModel.getInteractiveType());
                flowTaskTaskModel.setFlowId(flowInfoModel.getId());
                flowTaskTaskModel.setNodeId(detail.getNodeId());
                flowTaskTaskModel.setTaskStatus(TaskTaskStatusEnum.INITING.toValue());
                flowTaskTaskModelList.add(flowTaskTaskModel);
            }
        }
        flowTaskTaskService.bulkCreate(flowTaskTaskModelList);
        csceneFlowService.asyncAddTask(sceneId, request.getDetails(), flowTaskTaskModelList);
        log.debug("CsceneFlowService.addTask end.");
        return flowTaskTaskModelList;
    }

    // 1 视频/2 音频/3 普通数据包/4 邮件/5 聊天
    private String getTypeName(String type) {
        switch (type) {
            case "1":
                return "视频";
            case "2":
                return "音频";
            case "3":
                return "普通数据包";
            case "4":
                return "邮件";
            case "5":
                return "聊天";
            default:
                return "普通数据包";
        }
    }

    private SshInfo getSshInfo(List<SceneElementPortBO> portBOS, CloudScenePluginHandler handler) {
        String sourceIp = "";
        Integer sourcePort = null;
        String username = "";
        String password = "";
        for (var port : portBOS) {
            var pluginPort = handler.getPort(port.getId());
            if (CollectionUtils.isNotEmpty(pluginPort.getAccessModes())) {
                for (var a : pluginPort.getAccessModes()) {
                    if (AccessMode.Protocol.SSH.equals(a.getProtocol())) {
                        username = a.getUsername();
                        password = a.getPassword();
                        sourceIp = a.getAccessIp();
                        sourcePort = a.getAccessPort();
                    }
                }
            }
        }
        SshInfo sshInfo = new SshInfo();
        sshInfo.setIp(sourceIp);
        sshInfo.setPort(sourcePort.toString());
        sshInfo.setUsername(username);
        sshInfo.setPassword(password);
        return sshInfo;
    }

    private FlowInfoModel chooseFlow(String type) {
        String typeName = getTypeName(type);
        var optionMapper = BeanCommon.getBean(OptionMapper.class);
        var optionModel = optionMapper.first(new HumanCondition().filterEqual(OptionModel::getZhName, typeName)
                                                     .filterEqual(OptionModel::getType, "flow_category"));
        int category = Integer.parseInt(optionModel.getCode());
        var list = flowInfoMapper.list(new HumanCondition().filterEqual(FlowInfoModel::getCategory, category));
        Collections.shuffle(list);
        var chooseFlow = list.get(0);
        return chooseFlow;
    }

    private FlowTaskTaskModel addTask(FlowInfoModel chooseFlow, String csceneId, String targetNodeId) {
        AddFlowTaskFlowInfo flowTaskFlowInfo = new AddFlowTaskFlowInfo();
        flowTaskFlowInfo.setFlowId(chooseFlow.getId());
        List<AddFlowTaskFlowInfo> flowInfos = new ArrayList<>();
        flowInfos.add(flowTaskFlowInfo);
        AddFlowTaskTaskDetail detail = new AddFlowTaskTaskDetail();
        detail.setNodeId(targetNodeId);
        detail.setFlowInfos(flowInfos);
        List<AddFlowTaskTaskDetail> details = new ArrayList<>();
        details.add(detail);
        AddFlowTaskTaskRequest addFlowTaskTaskRequest = new AddFlowTaskTaskRequest();
        addFlowTaskTaskRequest.setId(csceneId);
        addFlowTaskTaskRequest.setDetails(details);
        var tasks = this.addTask(addFlowTaskTaskRequest);
        return tasks.get(0);
    }

    private void editTaskConfig(String taskId, String sourceIp, String targetIp) {
        // 调整下配置
        EditFlowTaskConfRequest confRequest = new EditFlowTaskConfRequest();
        confRequest.setConf("1");
        confRequest.setTaskId(taskId);
        confRequest.setKeepRunning(0);
        confRequest.setRateUnlimited(1);
        confRequest.setReplay(0);
        confRequest.setConfig(1);

        FlowTaskConf flowTaskConf = new FlowTaskConf();
        FlowTaskRateConf rateConf = new FlowTaskRateConf();
        rateConf.setLoadMode(0);
        flowTaskConf.setRateConf(rateConf);

        FlowTaskFlowConf flowConf = new FlowTaskFlowConf();
        flowConf.setIpv4Conf(new FlowTaskIpv4Conf());
        flowConf.getIpv4Conf().setSip(sourceIp);
        flowConf.getIpv4Conf().setDip(targetIp);
        flowConf.setTcpConf(new FlowTaskTcpConf());
        flowConf.getTcpConf().setMss(1460);
        flowTaskConf.setFlowConf(flowConf);

        FlowTaskCommonConf commonConf = new FlowTaskCommonConf();
        commonConf.setLatency(0);
        commonConf.setJitter(0);
        commonConf.setDuration(5);
        flowTaskConf.setCommonConf(commonConf);
        confRequest.setFlowTaskConf(flowTaskConf);
        csceneFlowService.editTaskConf(confRequest);
    }

    private PintTest getPingTest(SceneElementBO source, SceneElementBO target, CloudScenePluginHandler handler) {
        SshInfo sshInfo = this.getSshInfo(source.getPorts(), handler);
        String sourceIp = sshInfo.getIp();
        String sourcePort = sshInfo.getPort();
        String targetIp = target.getPorts().get(0).getIp();
        String username = sshInfo.getUsername();
        String password = sshInfo.getPassword();
        String res = PythonExecUtil.exec("pingtest.py", sourceIp, sourcePort, targetIp, username, password);
        var pingRes = JsonCommon.snakeParse(res, PintTest.class);
        return pingRes;
    }

    @Getter
    @Setter
    public static class ConnectInfo {
        private String taskId;
        private String linkId;
        private Boolean connected;
        private Double packetLoss;
        private Double delay;
        // 最大速率
        private String flow;
        // 发送的流量数据
        private Long obytes;
        private String sourceNumberId;
        private String targetNumberId;
    }

    @Getter
    @Setter
    public static class PintTest {
        private Boolean connected;
        private Double packetLoss;
        private Double delay;
        private String error;
    }

    @Getter
    @Setter
    private static class SshInfo {
        private String ip;
        private String port;
        private String username;
        private String password;
    }

    /**
     * 判断两个IP地址的前三部分是否一致
     * @return 如果前三部分一致，返回 true；否则返回 false
     */
    public static boolean areFirstThreeOctetsSame(String ip1, String ip2) {
        // 将IP地址按点分割成数组
        String[] octets1 = ip1.split("\\.");
        String[] octets2 = ip2.split("\\.");

        // 比较前三个八位组
        for (int i = 0; i < 3; i++) {
            if (!octets1[i].equals(octets2[i])) {
                return false;  // 如果任何一个八位组不同，返回false
            }
        }
        return true;  // 所有前三个八位组相同，返回true
    }

    /**
     * 计算时间差（秒）
     */
    private Long calculateSecondsBetween(Date startTime,Date endTime){
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startTime);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endTime);

//        毫秒
        long diffInMillis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();

//        返回秒
        return diffInMillis / 1000;
    }
}
