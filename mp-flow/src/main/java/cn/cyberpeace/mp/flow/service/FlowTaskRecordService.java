package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.bo.*;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import com.alibaba.fastjson.JSONObject;

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
     * 根据节点统计实时速率
     * @param sceneId
     * @return
     */
    List<FlowNodeTxBps> queryNodeTxBps(String sceneId);

    /**
     * 仿真统计
     * @param sceneId
     * @return
     */
    FlowStatisticsInfo flowStatistics(String sceneId);

    /**
     * 同步执行记录
     * @param jsonMessage
     */
    void syncRecord(JSONObject jsonMessage);

}

