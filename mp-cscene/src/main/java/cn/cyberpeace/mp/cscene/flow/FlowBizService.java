package cn.cyberpeace.mp.cscene.flow;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.cloud.VirtualMachine;
import cn.cyberpeace.mp.flow.v2.client.ixia.IxiaClient;
import cn.cyberpeace.mp.flow.v2.service.FlowGenerateService;
import cn.cyberpeace.mp.flow.v2.service.FlowGeneratorService;
import cn.cyberpeace.mp.flow.v2.service.FlowTaskService;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementPortMapper;
import org.apache.commons.lang3.StringUtils;
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

    public ServiceResult<Object> start(String flowTaskId, String flowGeneratorId, String targetId) {

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
        for (var p : ports) {
            networkId = p.getAuxiliaryNetworkId();
        }

        String ip = "";
        String mac = "";
        // 查询任务详情
        var flowTask = flowTaskService.get(flowTaskId).sourceObj();
        FlowTaskBO flowTaskBO = new FlowTaskBO();
        flowTaskBO.fromModel(flowTask);
        // 查询流量发生器详情
        var flowGenerator = flowGeneratorService.get(flowGeneratorId).sourceObj();
        FlowGeneratorBO flowGeneratorBO = new FlowGeneratorBO();
        flowGeneratorBO.fromModel(flowGenerator);
        // 创建网卡
        VirtualMachine virtualMachine = new VirtualMachine();
        var addPortResult = virtualMachine.addPort(targetId, "1ac74c73-b296-45d7-a3b8-0d69ecdf60eb");

        // 保存流量任务执行记录
        FlowTaskRecordBO taskRecord = new FlowTaskRecordBO();
        taskRecord.setId(TextUtils.uuid());
        taskRecord.setName(flowTaskBO.getName());
        taskRecord.setDesc(flowTaskBO.getDescription());
        taskRecord.setCategory(flowTaskBO.getCategory());
        taskRecord.setBandwidth(flowTaskBO.getBandwidth());
        taskRecord.setLoadMode(flowTaskBO.getLoadMode());
        taskRecord.setTargetIp(ip);
        taskRecord.setTargetMac(mac);
        taskRecord.setFlowGeneratorId(flowGeneratorId);
        taskRecord.setFlowGeneratorNicId("");
        taskRecord.setTrexMac(addPortResult.getMacAddress());

//        var flows = flowTaskBO.getFlows().stream().map(f -> {
//            FlowTaskRecordBO.TaskFlow taskFlow = new FlowTaskRecordBO.TaskFlow();
//            taskFlow.setFlowId(f.getFlowId());
//            taskFlow.setFlowGeneratorId(flowGeneratorId);
//            return taskFlow;
//        }).collect(Collectors.toList());
//
//        taskRecord.setFlows(flows);
//        taskRecord.setFilterProtocols(flowTaskBO.getFilterProtocols());
        // todo
        taskRecord.setStatus(0);
        flowGenerateService.startFlowTask(taskRecord);
        return new ServiceResult<>(true);
    }
}
