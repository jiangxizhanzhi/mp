package cn.cyberpeace.mp.cscene.flow;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskRecordMapper;
import cn.cyberpeace.mp.flow.v2.service.FlowGenerateService;
import cn.cyberpeace.mp.flow.v2.service.FlowGeneratorService;
import cn.cyberpeace.mp.flow.v2.service.FlowTaskService;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementPortMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/19
 */
@Service
public class FlowBizService {

    @Resource
    @Qualifier("flowTaskServiceV2")
    private FlowTaskService flowTaskService;

    @Resource
    private FlowGeneratorService flowGeneratorService;

    @Resource
    private FlowGenerateService flowGenerateService;

    @Resource
    private CloudPluginSceneElementMapper elementMapper;

    @Resource
    private CloudPluginSceneElementPortMapper portMapper;

    @Resource
    private FlowTaskRecordMapper flowTaskRecordMapper;

    public ServiceResult<FlowTaskRecordBO> start(String flowTaskId, String flowGeneratorId, String targetId) {

        // 启动流量任务,将 flowTaskId 的任务在 flowGeneratorId 执行,目标机器是 targetId
        // 查询靶机详情 ip、网络等
        var element = elementMapper.first(new HumanCondition()
                .filterEqual(CloudPluginSceneElementModel::getResourceId, targetId)
        );
        SceneHandler sceneHandler = new SceneHandler(element.getScene(), null, null);
        CloudScenePluginHandler plugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var e = plugin.getElement(element.getSceneElement());
        var ports = plugin.getPorts(e);
        String networkId = "";
        String ip = "";
        for (var p : ports) {
            networkId = p.getAuxiliaryNetworkId();
            ip = p.getSrcPort().getIp();
        }

        String mac = "";
        // 查询任务详情
        var flowTask = flowTaskService.get(flowTaskId).sourceObj();
        FlowTaskBO flowTaskBO = new FlowTaskBO();
        flowTaskBO.fromModel(flowTask);
        // 查询流量发生器详情 需要根据id 目标机器和任务 自动选择
        // 改用 chooseGenerator 方法
        var flowGenerator = flowGeneratorService.get(flowGeneratorId).sourceObj();
        FlowGeneratorBO flowGeneratorBO = new FlowGeneratorBO();
        flowGeneratorBO.fromModel(flowGenerator);

        // 保存流量任务执行记录
        FlowTaskRecordModel taskRecord = new FlowTaskRecordModel();
        taskRecord.setName(flowTaskBO.getName());
        taskRecord.setDescription(flowTaskBO.getDescription());
        taskRecord.setCategory(flowTaskBO.getCategory());
        taskRecord.setBandwidth(flowTaskBO.getBandwidth());
        taskRecord.setLoadMode(flowTaskBO.getLoadMode());
        taskRecord.setTargetIp(ip);
        taskRecord.setTargetMac(mac);
        taskRecord.setFlowGeneratorId(flowGeneratorId);

        var flows = flowTaskBO.getFlows().stream().map(f -> {
            FlowTaskRecordBO.TaskFlow taskFlow = new FlowTaskRecordBO.TaskFlow();
            taskFlow.setFlowId(f.getId());
            taskFlow.setFlowGeneratorId(flowGeneratorId);
            taskFlow.setDelay(f.getDelay());
            taskFlow.setWeight(f.getWeight());
            taskFlow.setName(f.getName());
            taskFlow.setPcapFile(f.getPcapFile());
            taskFlow.setResourceType(f.getResourceType());
            return taskFlow;
        }).collect(Collectors.toList());

        taskRecord.setFlows(JsonCommon.stringify(flows));
        taskRecord.setFilterProtocols(JsonCommon.stringify(flowTaskBO.getFilterProtocols()));
        // todo
        taskRecord.setStatus(0);
        flowTaskRecordMapper.create(taskRecord);
        FlowTaskRecordBO recordBO = new FlowTaskRecordBO();
        recordBO.fromModel(taskRecord);
        recordBO.setTargetNetworkId(networkId);
        recordBO.setTargetResourceId(targetId);
        flowGenerateService.startFlowTask(recordBO);
        return new ServiceResult<>(recordBO);
    }

    public ServiceResult<Boolean> pause(String taskRecordId) {
        var result = flowTaskRecordMapper.get(taskRecordId);
        FlowTaskRecordBO recordBO = new FlowTaskRecordBO();
        recordBO.fromModel(result);
        return flowGenerateService.pauseFlowTask(recordBO);
    }

    public ServiceResult<Boolean> stop(String taskRecordId) {
        var result = flowTaskRecordMapper.get(taskRecordId);
        FlowTaskRecordBO recordBO = new FlowTaskRecordBO();
        recordBO.fromModel(result);
        return flowGenerateService.stopFlowTask(recordBO);
    }

    public ServiceResult<Boolean> unpause(String taskRecordId) {
        var result = flowTaskRecordMapper.get(taskRecordId);
        FlowTaskRecordBO recordBO = new FlowTaskRecordBO();
        recordBO.fromModel(result);
        return flowGenerateService.unpauseFlowTask(recordBO);
    }

    public ServiceResult<Boolean> restart(String taskRecordId) {
        var result = flowTaskRecordMapper.get(taskRecordId);
        FlowTaskRecordBO recordBO = new FlowTaskRecordBO();
        recordBO.fromModel(result);
        return flowGenerateService.restartFlowTask(recordBO);
    }

    private FlowGeneratorBO chooseGenerator(FlowTaskBO task, CloudPluginSceneElementBO target) {
        return new FlowGeneratorBO();
    }
}
