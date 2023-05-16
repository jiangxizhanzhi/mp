package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.bo.FlowRealTimeRate;
import cn.cyberpeace.mp.flow.bo.FlowStatisticalCondition;
import cn.cyberpeace.mp.flow.bo.FlowSummary;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskRecordMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskRecordFields;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FlowTaskRecordServiceImpl extends BaseModelServiceImpl<FlowTaskRecordModel, FlowTaskRecordMapper, FlowTaskRecordFields,
        QueryParams> implements FlowTaskRecordService {

    public FlowTaskRecordServiceImpl(FlowTaskRecordMapper stuMapper, FlowTaskRecordFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 根据taskId查询流量任务记录
     * @param taskId
     * @return
     */
    @Override
    public List<FlowTaskRecordModel> findByTaskId(String taskId) {
        log.info("FlowTaskRecordServiceImpl.findByTaskId begin. taskId:{}", taskId);
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskRecordModel::getTaskId, taskId);
        condition.orderBy("-createTime");
        List<FlowTaskRecordModel> flowTaskConfModelList = getModelMapper().list(condition);
        log.info("FlowTaskRecordServiceImpl.findByTaskId end. flowTaskConfModelList:{}", flowTaskConfModelList);
        return flowTaskConfModelList;
    }

    /**
     * 根据taskId删除
     * @param taskId
     */
    @Override
    public void deleteByTaskId(String taskId) {
        log.info("FlowTaskRecordServiceImpl.deleteByTaskId begin. taskId:{}", taskId);
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskRecordModel::getTaskId, taskId);
        getModelMapper().filterDelete(condition);
        log.info("FlowTaskRecordServiceImpl.deleteByTaskId end.");
    }

    /**
     * 批量统计每一个流量任务的实时速率
     * @param taskIds
     * @param conditions
     * @return
     */
    @Override
    public Map<String, List<FlowRealTimeRate>> realTimeRate(List<String> taskIds, List<FlowStatisticalCondition> conditions) {
        log.info("FlowTaskRecordServiceImpl.realTimeRate begin. taskIds:{}, condition:{}", taskIds, conditions);
        List<FlowRealTimeRate> flowRealTimeRateList = this.getModelMapper().realTimeRateByTaskId(taskIds, conditions);
        Map<String, List<FlowRealTimeRate>> result = new HashMap<String, List<FlowRealTimeRate>>();
        if (CollectionUtils.isNotEmpty(flowRealTimeRateList)) {
            String taskId = "";
            List<FlowRealTimeRate> mapList = new ArrayList<>();
            for (FlowRealTimeRate temp : flowRealTimeRateList) {
                if (taskId.equals(temp.getTaskId())) {
                    mapList.add(temp);
                } else {
                    if (StringUtils.isNotEmpty(taskId) && CollectionUtils.isNotEmpty(mapList)) {
                        result.put(taskId, mapList);
                    }
                    mapList = new ArrayList<>();
                    taskId = temp.getTaskId();
                    mapList.add(temp);
                }
            }
            if (StringUtils.isNotEmpty(taskId) && CollectionUtils.isNotEmpty(mapList)) {
                result.put(taskId, mapList);
            }
        }
        log.info("FlowTaskRecordServiceImpl.realTimeRate end. result:{}", result);
        return result;
    }

    /**
     * 仿真概况
     * @param sceneId
     * @return
     */
    @Override
    public FlowSummary queryFlowSummary(String sceneId) {
        log.info("FlowTaskRecordServiceImpl.queryFlowSummary begin. id:{}", sceneId);
        FlowSummary flowSummary = this.getModelMapper().queryFlowSummary(sceneId);
        log.info("FlowTaskRecordServiceImpl.queryFlowSummary end. flowSummary:{}", flowSummary);
        return flowSummary;
    }

}