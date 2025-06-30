package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.bo.AttackType;
import cn.cyberpeace.mp.base.bo.ImageToolRequest;
import cn.cyberpeace.mp.base.bo.OutAttackToolRequest;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
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
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.FlowTaskCommonConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskFlowConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskIpv4Conf;
import cn.cyberpeace.mp.flow.bo.FlowTaskRateConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskTcpConf;
import cn.cyberpeace.mp.flow.enums.TaskTaskStatusEnum;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @ApiOperation("使用ping检测源和目标节点的联通性")
    @PostMapping("/test_ping")
    public ServiceResult<PintTest> pingTest(@RequestBody OutStartFlowTaskRequest request) {
        // 查找源节点和目标节点的信息
        var sceneId = csceneInstanceService.getSceneId(request.getId());
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, false);
        var source = sceneHandler.getElement(request.getSourceNode());
        var target = sceneHandler.getElement(request.getTargetNode());
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
        String res = PythonExecUtil.exec("pingtest.py", sourceIp, String.valueOf(sourcePort), targetIp, username, password);
        var pingRes = JsonCommon.snakeParse(res, PintTest.class);
        return new ServiceResult(pingRes).asResult();
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("启动攻击工具")
    @PostMapping("/attack")
    public void attack(@RequestBody OutAttackToolRequest request) {
        var sceneId = csceneInstanceService.getSceneId(request.getId());
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, false);
        CloudScenePluginHandler handler = sceneHandler.getPluginHandler("cpcloud");
        var source = sceneHandler.getElement(request.getSourceNode());
        var target = sceneHandler.getElement(request.getTargetNode());
        var pluginSource = handler.getElement(source.getId());
        var pluginTarget = handler.getElement(target.getId());
        // 取源节点上的ssh信息
        SshInfo sshInfo = this.getSshInfo(source.getPorts(), handler);

        // 取到指定工具
        ImageToolRequest tool = null;
        for (var t : pluginSource.getAttackTools()) {
            if (StringUtils.equals(t.getId(), request.getToolId())) {
                tool = t;
            }
        }
        tool.setParams(request.getParams());

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
        PythonExecUtil.exec("ssh_exec.py", encodeParams);
        // 启动一个线程 查询攻击结果 每隔5秒查询一次es数据 ， 查最新的一条数据
        CollectionScenePluginHandler collectionScenePluginHandler = sceneHandler.getPluginHandler("cpcollection");
        attackDataOutputUtil.startOneAttackDataOutputTask(collectionScenePluginHandler,source , target, tool);
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
}
