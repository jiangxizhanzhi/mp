package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.bo.*;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;


public interface FlowTaskRecordService extends BaseModelService<FlowTaskRecordModel, QueryParams> {

    
    List<FlowTaskRecordModel> findByTaskId(String taskId);

    
    void deleteByTaskId(String taskId);

    
    Map<String, List<FlowRealTimeRate>> realTimeRate(List<String> taskIds, List<FlowStatisticalCondition> conditions);

    
    FlowSummary queryFlowSummary(String sceneId);

    
    List<FlowNodeTxBps> queryNodeTxBps(String sceneId);

    
    FlowStatisticsInfo flowStatistics(String sceneId);

    
    void syncRecord(JSONObject jsonMessage);

    
    void batchUpdateStatus(int status, List<String> taskIds);

}

