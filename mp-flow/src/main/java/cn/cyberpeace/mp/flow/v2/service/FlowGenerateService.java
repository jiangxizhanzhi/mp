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
    private TrexFlowGenerateService trexFlowGenerateService;

    @Resource
    private IxiaFlowGenerateService ixiaFlowGenerateService;

    @Resource
    private FlowStartPostProcessor flowStartPostProcessor;

    public ServiceResult<Boolean> startFlowTask(FlowTaskRecordBO flowTaskRecordBO) {
        // 查询流量发生器详情
        String flowGeneratorId = flowTaskRecordBO.getFlowGeneratorId();
        var flowGenerator = flowGeneratorService.get(flowGeneratorId).sourceObj();
        FlowGeneratorBO flowGeneratorBO = new FlowGeneratorBO();
        flowGeneratorBO.fromModel(flowGenerator);
        if ("cpcloud_trex".equals(flowGenerator.getType())) {
            FlowCallBackTask callBack = trexFlowGenerateService.startFlowTask(flowTaskRecordBO, flowGeneratorBO);
            flowStartPostProcessor.addTask(callBack);
        } else if ("cpcloud_ixia".equals(flowGenerator.getType())) {
            FlowCallBackTask callBack = ixiaFlowGenerateService.startFlowTask(flowTaskRecordBO, flowGeneratorBO);
            flowStartPostProcessor.addTask(callBack);
        }

        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> stopFlowTask(String flowTaskRecordId) {
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> pauseFlowTask(String flowTaskRecordId) {
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> unpauseFlowTask(String flowTaskRecordId) {
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> restartFlowTask(String flowTaskRecordId) {
        return new ServiceResult<>(true);
    }

}
