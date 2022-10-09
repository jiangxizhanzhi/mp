package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import org.springframework.stereotype.Service;

@Service
public class FlowGenerateService {

    public ServiceResult<Boolean> startFlowTask(FlowTaskRecordBO flowTaskRecordBO) {
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
