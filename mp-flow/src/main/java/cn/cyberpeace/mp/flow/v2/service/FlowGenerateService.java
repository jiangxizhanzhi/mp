package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FlowGenerateService {

    @Resource
    private FlowGeneratorService flowGeneratorService;

    @Resource
    private IGenerateService trexFlowGenerateService;

    @Resource
    private IGenerateService ixiaFlowGenerateService;

    @Resource
    private FlowStartPostProcessor flowStartPostProcessor;

    public ServiceResult<Boolean> startFlowTask(FlowTaskRecordBO flowTaskRecordBO) {
        FlowGeneratorBO flowGeneratorBO = this.getFlowGeneratorBO(flowTaskRecordBO.getFlowGeneratorId());
        if (flowGeneratorBO.getType().equals("cpcloud_ixia")) {
            // todo 查询ixia的网络id
            flowTaskRecordBO.setTargetNetworkId("937f5af1-2510-4b8e-9e1d-959c4dc6939d");
        }
        FlowCallBackTask callBack = this.getActionGenerateService(flowGeneratorBO).startFlowTask(flowTaskRecordBO, flowGeneratorBO);
        flowStartPostProcessor.addTask(callBack);
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> stopFlowTask(FlowTaskRecordBO flowTaskRecordBO) {
        FlowGeneratorBO flowGeneratorBO = this.getFlowGeneratorBO(flowTaskRecordBO.getFlowGeneratorId());
        FlowCallBackTask callBack = this.getActionGenerateService(flowGeneratorBO).stopFlowTask(flowTaskRecordBO, flowGeneratorBO);
        flowStartPostProcessor.addTask(callBack);
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> pauseFlowTask(FlowTaskRecordBO flowTaskRecordBO) {
        FlowGeneratorBO flowGeneratorBO = this.getFlowGeneratorBO(flowTaskRecordBO.getFlowGeneratorId());
        FlowCallBackTask callBack = this.getActionGenerateService(flowGeneratorBO).pauseFlowTask(flowTaskRecordBO, flowGeneratorBO);
        flowStartPostProcessor.addTask(callBack);
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> unpauseFlowTask(FlowTaskRecordBO flowTaskRecordBO) {
        FlowGeneratorBO flowGeneratorBO = this.getFlowGeneratorBO(flowTaskRecordBO.getFlowGeneratorId());
        FlowCallBackTask callBack = this.getActionGenerateService(flowGeneratorBO).unpauseFlowTask(flowTaskRecordBO, flowGeneratorBO);
        flowStartPostProcessor.addTask(callBack);
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> restartFlowTask(FlowTaskRecordBO flowTaskRecordBO) {
        FlowGeneratorBO flowGeneratorBO = this.getFlowGeneratorBO(flowTaskRecordBO.getFlowGeneratorId());
        FlowCallBackTask callBack = this.getActionGenerateService(flowGeneratorBO).restartFlowTask(flowTaskRecordBO, flowGeneratorBO);
        flowStartPostProcessor.addTask(callBack);
        return new ServiceResult<>(true);
    }

    private IGenerateService getActionGenerateService(FlowGeneratorBO flowGeneratorBO) {
        // 查询流量发生器详情
        if ("cpcloud_trex".equals(flowGeneratorBO.getType())) {
            return trexFlowGenerateService;
        } else if ("cpcloud_ixia".equals(flowGeneratorBO.getType())) {
            return ixiaFlowGenerateService;
        }
        return null;
    }

    private FlowGeneratorBO getFlowGeneratorBO(String flowGeneratorId) {
        var flowGenerator = flowGeneratorService.get(flowGeneratorId).sourceObj();
        FlowGeneratorBO flowGeneratorBO = new FlowGeneratorBO();
        flowGeneratorBO.fromModel(flowGenerator);
        return flowGeneratorBO;
    }
}
