package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.bo.FlowRealTimeRate;
import cn.cyberpeace.mp.flow.bo.FlowStatisticalCondition;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsInfo;
import cn.cyberpeace.mp.flow.bo.FlowSummary;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;

import java.util.List;
import java.util.Map;

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

    /**
     * 批量统计每一个流量任务的实时速率
     * @param taskIds
     * @param conditions
     * @return
     */
    Map<String, List<FlowRealTimeRate>> realTimeRate(List<String> taskIds, List<FlowStatisticalCondition> conditions);

    /**
     * 仿真概况
     * @param sceneId
     * @return
     */
    FlowSummary queryFlowSummary(String sceneId);

    /**
     * 仿真统计
     * @param sceneId
     * @return
     */
    FlowStatisticsInfo flowStatistics(String sceneId);

}

