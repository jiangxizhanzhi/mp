package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskRecordMapper;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
public class IxiaFlowCallBackTask implements FlowCallBackTask {

    private FlowTaskRecordBO flowTaskRecordBO;

    public IxiaFlowCallBackTask(FlowTaskRecordBO flowTaskRecordBO) {
        this.flowTaskRecordBO = flowTaskRecordBO;
    }

    @Override
    public boolean action() {
        // todo
        FlowTaskRecordMapper recordMapper = BeanCommon.getBean(FlowTaskRecordMapper.class);
        FlowTaskRecordModel recordModel = new FlowTaskRecordModel();
        recordModel.setId(flowTaskRecordBO.getId());
        recordModel.setStatus(1);
        recordMapper.update(recordModel);
        // 查询任务状态 包括流量、速率、包数量、tcp数量等
        return true;
    }
}