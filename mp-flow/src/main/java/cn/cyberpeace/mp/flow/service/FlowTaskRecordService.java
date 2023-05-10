package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;

import java.util.List;

/**
 * @Description 流量任务记录service
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
public interface FlowTaskRecordService extends BaseModelService<FlowTaskRecordModel, QueryParams> {

    /**
     * 根据taskId查询流量任务记录
     * @param taskId
     * @return
     */
    List<FlowTaskRecordModel> findByTaskId(String taskId);

    /**
     * 根据taskId删除
     * @param taskId
     */
    void deleteByTaskId(String taskId);

}

