package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.bo.*;
import cn.cyberpeace.mp.flow.enums.TaskTaskStatusEnum;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskRecordMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.response.BatchActuatorCallbackResponse;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskRecordFields;
import cn.cyberpeace.mp.flow.util.FlowCacheUtil;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

@Service
@Slf4j
public class FlowTaskRecordServiceImpl extends BaseModelServiceImpl<FlowTaskRecordModel, FlowTaskRecordMapper, FlowTaskRecordFields,
        QueryParams> implements FlowTaskRecordService {

    @Autowired
    private FlowTaskTaskMapper flowTaskTaskMapper;

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
        condition.orderBy("createTime");
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
     * 根据节点统计实时速率
     * @param sceneId
     * @return
     */
    @Override
    public List<FlowNodeTxBps> queryNodeTxBps(String sceneId) {
        log.info("FlowTaskRecordServiceImpl.queryNodeTxBps begin. id:{}", sceneId);
        List<FlowNodeTxBps> flowNodeTxBpsList = this.getModelMapper().queryNodeTxBps(sceneId);
        log.info("FlowTaskRecordServiceImpl.queryNodeTxBps end. flowNodeTxBpsList:{}", flowNodeTxBpsList);
        return flowNodeTxBpsList;
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
        flowStatisticsInfo.setTotalTxL2(0L);
        flowStatisticsInfo.setTotalTxL1(0L);
        flowStatisticsInfo.setTotalPps(0L);
        flowStatisticsInfo.setTotalRx(0L);
        flowStatisticsInfo.setQueueFull(0L);
        flowStatisticsInfo.setDropRate(0L);
        flowStatisticsInfo.setObytes(0L);
        flowStatisticsInfo.setOerrors(0L);
        List<FlowStatisticalCondition> conditionList = this.getCondition(sceneId);
        if (CollectionUtils.isEmpty(conditionList)) {
            log.info("没有执行记录");
            return flowStatisticsInfo;
        }
        List<FlowStatisticsTemp> tempList = this.getModelMapper().statisticsTaskId(sceneId, conditionList);
        List<FlowStatisticsTemp> tempContainOldList = this.getModelMapper().statisticsTaskIdContainOld(sceneId, conditionList);
        List<FlowStatisticsDetail> txBpsList = new ArrayList<>();
        List<FlowStatisticsDetail> txPpsList = new ArrayList<>();
        List<FlowStatisticsDetail> latencyList = new ArrayList<>();
        List<FlowStatisticsDetail> jitterList = new ArrayList<>();
        List<FlowStatisticsDetail> obytesList = new ArrayList<>();
        List<FlowStatisticsDetail> oerrorsList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tempList)) {
            for (FlowStatisticsTemp temp : tempList) {
                if (temp.getOffsetValue() == 0) {
                    flowStatisticsInfo.setTotalTxL2(temp.getTotalTxL2() == null ? 0 : temp.getTotalTxL2());
                    flowStatisticsInfo.setTotalTxL1(temp.getTotalTxL1() == null ? 0 : temp.getTotalTxL1());
                    flowStatisticsInfo.setTotalRx(temp.getTotalRx() == null ? 0 : temp.getTotalRx());
                    flowStatisticsInfo.setQueueFull(temp.getQueueFull() == null ? 0 : temp.getQueueFull());
                    flowStatisticsInfo.setDropRate(temp.getDropRate() == null ? 0 : temp.getDropRate());
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
        if (CollectionUtils.isNotEmpty(tempContainOldList)) {
            for (FlowStatisticsTemp temp : tempContainOldList) {
                if (temp.getOffsetValue() == 0) {
                    flowStatisticsInfo.setTotalPps(temp.getTotalPps() == null ? 0 : temp.getTotalPps());
                    flowStatisticsInfo.setObytes(temp.getObytes() == null ? 0 : temp.getObytes());
                    flowStatisticsInfo.setOerrors(temp.getOerrors() == null ? 0 : temp.getOerrors());
                }
                FlowStatisticsDetail obytes = new FlowStatisticsDetail();
                obytes.setStatisticalTime(temp.getStatisticalTime());
                obytes.setOffsetUnit(temp.getOffsetUnit());
                obytes.setOffsetValue(temp.getOffsetValue());
                obytes.setValue(temp.getObytes());
                obytesList.add(obytes);
                FlowStatisticsDetail oerrors = new FlowStatisticsDetail();
                oerrors.setStatisticalTime(temp.getStatisticalTime());
                oerrors.setOffsetUnit(temp.getOffsetUnit());
                oerrors.setOffsetValue(temp.getOffsetValue());
                oerrors.setValue(temp.getOerrors());
                oerrorsList.add(oerrors);
            }
        }
        flowStatisticsInfo.setTxBpsList(txBpsList);
        flowStatisticsInfo.setTxPpsList(txPpsList);
        flowStatisticsInfo.setLatencyList(latencyList);
        flowStatisticsInfo.setJitterList(jitterList);
        flowStatisticsInfo.setObytesList(obytesList);
        flowStatisticsInfo.setOerrorsList(oerrorsList);
        log.info("FlowTaskRecordServiceImpl.flowStatistics end. flowStatisticsInfo:{}", flowStatisticsInfo);
        return flowStatisticsInfo;
    }

    /**
     * 同步执行记录
     * @param jsonMessage
     */
    @Override
    public void syncRecord(JSONObject jsonMessage) {
        log.info("FlowTaskRecordServiceImpl.syncRecord start. jsonMessage:{}", jsonMessage);
        if (jsonMessage == null) {
            log.info("消息为空或为失败消息，不做处理");
            return;
        }
        Integer status = jsonMessage.getInteger("status");
        String taskId = jsonMessage.getString("task_id");
        String msg = null;
        FlowTaskTaskModel flowTaskTaskModel = flowTaskTaskMapper.get(taskId);
        flowTaskTaskModel.setTaskStatus(status);
        if (TaskTaskStatusEnum.STOPPED.toValue() == status) {
            flowTaskTaskModel.setSuccessCount(flowTaskTaskModel.getSuccessCount() + 1);
        } else if (TaskTaskStatusEnum.ERROR.toValue() == status) {
            msg = jsonMessage.getString("msg");
            flowTaskTaskModel.setErrorCount(flowTaskTaskModel.getErrorCount() + 1);
        }
        flowTaskTaskMapper.update(flowTaskTaskModel);

        if (Objects.nonNull(jsonMessage.get("flow_record"))) {
            JSONObject data = jsonMessage.getJSONObject("flow_record");
            FlowTaskRecordModel flowTaskRecordModel = new FlowTaskRecordModel();
            flowTaskRecordModel.setTaskId(data.getString("task_uuid"));
            flowTaskRecordModel.setTxBps(data.getLong("tx_bps"));
            flowTaskRecordModel.setTxPps(data.getLong("tx_pps"));
            flowTaskRecordModel.setLatency(data.getLong("latency"));
            flowTaskRecordModel.setJitter(data.getLong("jitter"));
            flowTaskRecordModel.setObytes(data.getLong("obytes"));
            flowTaskRecordModel.setOerrors(data.getLong("oerrors"));
            flowTaskRecordModel.setTotalTxL2(data.getLong("total_tx_L2"));
            flowTaskRecordModel.setTotalTxL1(data.getLong("total_tx_L1"));
            flowTaskRecordModel.setTotalPps(data.getLong("opackets"));
            flowTaskRecordModel.setTotalRx(data.getLong("total_rx"));
            flowTaskRecordModel.setQueueFull(data.getLong("queue_full"));
            flowTaskRecordModel.setDropRate(data.getLong("drop_rate"));
            flowTaskRecordModel.setRecordStatus(data.getInteger("status"));
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            flowTaskRecordModel.setCreateTime(LocalDateTime.parse(data.getString("record_time"), df));
            //flowTaskRecordModel.setCreateTime(StringUtils.isEmpty(data.getString("record_time")) ? null : LocalDateTime.parse(data.getString("record_time"), df));
            this.getModelMapper().save(flowTaskRecordModel);
        }

        // 任务回调处理
        this.taskCallback(flowTaskTaskModel, msg);
        log.info("FlowTaskRecordServiceImpl.syncRecord end.");
    }

    /**
     * 任务回调
     * @param flowTaskTaskModel
     * @param msg
     */
    private void taskCallback(FlowTaskTaskModel flowTaskTaskModel, String msg) {
        String taskId = flowTaskTaskModel.getId();
        Runnable taskRunnable = FlowCacheUtil.taskRunnableCache.getIfPresent(FlowCacheUtil.RUNNABLE_PREFIX + taskId);
        if (Objects.nonNull(taskRunnable)) {
            log.info("call task runnable for: {}", taskId);
            taskRunnable.run();
            FlowCacheUtil.taskRunnableCache.invalidate(FlowCacheUtil.RUNNABLE_PREFIX + taskId);
        }

        if (StringUtils.isNotBlank(msg)) {
            // 有错误信息，代表执行异常
            Consumer taskConsumer = FlowCacheUtil.taskConsumerCache.getIfPresent(FlowCacheUtil.CONSUMER_PREFIX + taskId);
            if (Objects.nonNull(taskConsumer)) {
                log.info("call task consumer for: {}", taskId);
                taskConsumer.accept(msg);
                FlowCacheUtil.taskConsumerCache.invalidate(FlowCacheUtil.CONSUMER_PREFIX + taskId);
            }
        }

        // 批量执行校验
        String sceneId = flowTaskTaskModel.getSceneId();
        String nodeId = flowTaskTaskModel.getNodeId();
        log.info("batch tasks run for sceneId: {}", sceneId);
        Consumer batchTaskConsumer = FlowCacheUtil.taskConsumerCache.getIfPresent(FlowCacheUtil.CONSUMER_PREFIX + sceneId);
        if (Objects.nonNull(batchTaskConsumer)) {
            // 判断任务是否都结束
            HumanCondition condition = new HumanCondition();
            condition.filterEqual(FlowTaskTaskModel::getSceneId, sceneId);
            condition.filterEqual(FlowTaskTaskModel::getNodeId, nodeId);
            List<FlowTaskTaskModel> taskTaskModels = flowTaskTaskMapper.list(condition);
            if (CollectionUtils.isNotEmpty(taskTaskModels)) {
                int runningCount = (int) taskTaskModels.stream().filter(taskTaskModel -> taskTaskModel.getTaskStatus() == TaskTaskStatusEnum.RUNNING.toValue()).count();
                if (runningCount == 0) {
                    // 回调参数封装
                    BatchActuatorCallbackResponse batchActuatorCallbackResponse= new BatchActuatorCallbackResponse();
                    List<FlowTaskModel> successTaskModels = new ArrayList<>();
                    List<FlowTaskModel> errorTaskModels = new ArrayList<>();
                    taskTaskModels.forEach(taskTaskModel -> {
                        FlowTaskModel flowTaskModel = new FlowTaskModel();
                        flowTaskModel.setFlowId(taskTaskModel.getFlowId());
                        flowTaskModel.setId(taskTaskModel.getId());
                        if (TaskTaskStatusEnum.STOPPED.toValue() == taskTaskModel.getTaskStatus()) {
                            successTaskModels.add(flowTaskModel);
                        } else {
                            errorTaskModels.add(flowTaskModel);
                        }
                    });
                    batchActuatorCallbackResponse.setSuccessTaskModels(successTaskModels);
                    batchActuatorCallbackResponse.setErrorTaskModels(errorTaskModels);

                    log.info("batch tasks accept consumer: {}", batchActuatorCallbackResponse);
                    batchTaskConsumer.accept(batchActuatorCallbackResponse);
                    FlowCacheUtil.taskConsumerCache.invalidate(FlowCacheUtil.CONSUMER_PREFIX + sceneId);

                    Runnable batchTaskRunnable = FlowCacheUtil.taskRunnableCache.getIfPresent(FlowCacheUtil.RUNNABLE_PREFIX + sceneId);
                    if (Objects.nonNull(batchTaskRunnable)) {
                        log.info("batch tasks run runnable");
                        batchTaskRunnable.run();
                        FlowCacheUtil.taskRunnableCache.invalidate(FlowCacheUtil.RUNNABLE_PREFIX + sceneId);
                    }
                }
            } else {
                log.error("no batch tasks for sceneId: {}", sceneId);
            }
        } else {
            log.info("batch tasks batchTaskConsumer is null");
        }
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
        Integer diff = 5;
        String offsetUnit = "s";
        if (totalDiff <= 60) {
            diff = 5;
        } else if (totalDiff > 60 && totalDiff <= 120) {
            diff = 10;
        } else if (totalDiff > 120 && totalDiff <= 300) {
            diff = 30;
        } else if (totalDiff > 300 && totalDiff <= 600) {
            diff = 1;
            offsetUnit = "m";
        } else if (totalDiff > 600 && totalDiff <= 1800) {
            diff = 3;
            offsetUnit = "m";
        } else if (totalDiff > 1800 && totalDiff <= 3600) {
            diff = 5;
            offsetUnit = "m";
        } else if (totalDiff > 3600 && totalDiff <= 3600*2) {
            diff = 10;
            offsetUnit = "m";
        } else if (totalDiff > 3600*2 && totalDiff <= 3600*5) {
            diff = 30;
            offsetUnit = "m";
        } else {
            diff = 1;
            offsetUnit = "h";
        }
        List<FlowStatisticalCondition> queryList = new ArrayList<FlowStatisticalCondition>();
        Integer offSet = 0;
        while (endTime.isAfter(startTime)) {
            FlowStatisticalCondition flowStatisticalCondition = new FlowStatisticalCondition();
            flowStatisticalCondition.setEndTime(endTime);
            if ("h".equals(offsetUnit)) {
                flowStatisticalCondition.setStartTime(endTime.minusSeconds(diff * 3600));
                endTime = endTime.minusSeconds(diff * 3600);
            } else if ("m".equals(offsetUnit)) {
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