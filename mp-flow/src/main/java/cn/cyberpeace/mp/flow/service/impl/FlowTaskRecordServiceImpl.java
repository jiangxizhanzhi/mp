package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.bo.*;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskRecordMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskRecordFields;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
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

    /**
     * 仿真统计
     * @param sceneId
     * @return
     */
    @Override
    public FlowStatisticsInfo flowStatistics(String sceneId) {
        log.info("FlowTaskRecordServiceImpl.flowStatistics begin. id:{}", sceneId);
        FlowStatisticsInfo flowStatisticsInfo = new FlowStatisticsInfo();
        List<FlowStatisticalCondition> conditionList = this.getCondition(sceneId);
        List<FlowStatisticsTemp> tempList = this.getModelMapper().statisticsTaskId(sceneId, conditionList);
        List<FlowStatisticsDetail> txBpsList = new ArrayList<>();
        List<FlowStatisticsDetail> txPpsList = new ArrayList<>();
        List<FlowStatisticsDetail> latencyList = new ArrayList<>();
        List<FlowStatisticsDetail> jitterList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tempList)) {
            for (FlowStatisticsTemp temp : tempList) {
                if (temp.getOffsetValue() == 0) {
                    flowStatisticsInfo.setTotalTxL2(temp.getTotalTxL2());
                    flowStatisticsInfo.setTotalTxL1(temp.getTotalTxL1());
                    flowStatisticsInfo.setTotalPps(temp.getTotalPps());
                    flowStatisticsInfo.setTotalRx(temp.getTotalRx());
                    flowStatisticsInfo.setQueueFull(temp.getQueueFull());
                    flowStatisticsInfo.setDropRate(temp.getDropRate());
                }
                FlowStatisticsDetail txBps = new FlowStatisticsDetail();
                txBps.setStatisticalTime(temp.getStatisticalTime());
                txBps.setOffsetUnit(temp.getOffsetUnit());
                txBps.setOffsetValue(temp.getOffsetValue());
                txBps.setValue(temp.getTxBps());
                txBpsList.add(txBps);
                FlowStatisticsDetail txPps = new FlowStatisticsDetail();
                txPps.setStatisticalTime(temp.getStatisticalTime());
                txPps.setOffsetUnit(temp.getOffsetUnit());
                txPps.setOffsetValue(temp.getOffsetValue());
                txPps.setValue(temp.getTxPps());
                txPpsList.add(txPps);
                FlowStatisticsDetail latency = new FlowStatisticsDetail();
                latency.setStatisticalTime(temp.getStatisticalTime());
                latency.setOffsetUnit(temp.getOffsetUnit());
                latency.setOffsetValue(temp.getOffsetValue());
                latency.setValue(temp.getLatency());
                latencyList.add(latency);
                FlowStatisticsDetail jitter = new FlowStatisticsDetail();
                jitter.setStatisticalTime(temp.getStatisticalTime());
                jitter.setOffsetUnit(temp.getOffsetUnit());
                jitter.setOffsetValue(temp.getOffsetValue());
                jitter.setValue(temp.getJitter());
                jitterList.add(jitter);
            }
        }
        flowStatisticsInfo.setTxBpsList(txBpsList);
        flowStatisticsInfo.setTxPpsList(txPpsList);
        flowStatisticsInfo.setLatencyList(latencyList);
        flowStatisticsInfo.setJitterList(jitterList);
        log.info("FlowTaskRecordServiceImpl.flowStatistics end. flowStatisticsInfo:{}", flowStatisticsInfo);
        return flowStatisticsInfo;
    }

    /**
     * 获取统计条件
     * @return
     */
    private List<FlowStatisticalCondition> getCondition(String sceneId) {
        log.info("FlowTaskRecordServiceImpl.getCondition begin. sceneId:{}", sceneId);
        LocalDateTime startTime = this.getModelMapper().getFirstTime(sceneId);
        if (startTime == null) {
            return null;
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        Long totalDiff = duration.toSeconds();
        if (totalDiff > 3600 * 12) {
            totalDiff = 3600 * 12L;
            startTime = endTime.minusSeconds(3600 * 12);
        }
        Integer diff = 2;
        String offsetUnit = "s";
        if (totalDiff <= 60) {
            diff = 2;
        } else if (totalDiff > 60 && totalDiff <= 300) {
            diff = 5;
        } else if (totalDiff > 300 && totalDiff <= 1800) {
            diff = 30;
        } else if (totalDiff > 1800 && totalDiff <= 3600*2) {
            diff = 2;
            offsetUnit = "m";
        } else {
            diff = 10;
            offsetUnit = "m";
        }
        List<FlowStatisticalCondition> queryList = new ArrayList<FlowStatisticalCondition>();
        Integer offSet = 0;
        while (endTime.isAfter(startTime)) {
            FlowStatisticalCondition flowStatisticalCondition = new FlowStatisticalCondition();
            flowStatisticalCondition.setEndTime(endTime);
            if ("m".equals(offsetUnit)) {
                flowStatisticalCondition.setStartTime(endTime.minusSeconds(diff * 60));
                endTime = endTime.minusSeconds(diff * 60);
            } else if ("s".equals(offsetUnit)) {
                flowStatisticalCondition.setStartTime(endTime.minusSeconds(diff));
                endTime = endTime.minusSeconds(diff);
            }
            flowStatisticalCondition.setOffsetUnit(offsetUnit);
            flowStatisticalCondition.setOffsetValue(offSet);
            queryList.add(flowStatisticalCondition);
            offSet -= diff;
        }
        log.info("FlowTaskRecordServiceImpl.getCondition end. queryList:{}", queryList);
        return queryList;
    }

}