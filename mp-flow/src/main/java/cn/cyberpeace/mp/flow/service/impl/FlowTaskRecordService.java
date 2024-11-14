// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowTaskRecordServiceImpl.java.
// File size 20743.
// File create date Fri Jul 12 12:55:24 CST 2024.
// File update date Fri Jul 12 12:55:24 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.bo.FlowNodeTxBps;
import cn.cyberpeace.mp.flow.bo.FlowRealTimeRate;
import cn.cyberpeace.mp.flow.bo.FlowStatisticalCondition;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsDetail;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsInfo;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsTemp;
import cn.cyberpeace.mp.flow.bo.FlowSummary;
import cn.cyberpeace.mp.flow.enums.TaskTaskStatusEnum;
import cn.cyberpeace.mp.flow.integration.actuator.FlowActuator;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskRecordMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.response.BatchActuatorResponse;
import cn.cyberpeace.mp.flow.service.FlowTaskPostService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskRecordFields;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName FlowTaskRecordServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowTaskRecordService extends BaseModelServiceImpl<FlowTaskRecordModel, FlowTaskRecordMapper, FlowTaskRecordFields, QueryParams> {

    @Autowired
    private FlowTaskTaskMapper flowTaskTaskMapper;

    @Resource
    private FlowTaskPostService flowPostService;

    @Resource
    private FlowActuatorMapper flowActuatorMapper;


    public FlowTaskRecordService(FlowTaskRecordMapper stuMapper, FlowTaskRecordFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 仿真概况
     * @param actuatorId
     * @return
     */
    public FlowSummary queryFlowSummary(String actuatorId) {
        FlowSummary flowSummary = this.getModelMapper().queryFlowSummary(actuatorId);
        return flowSummary;
    }

    /**
     * 根据节点统计实时速率
     * @param actuatorId
     * @return
     */
    public List<FlowNodeTxBps> queryNodeTxBps(String actuatorId) {
        log.info("FlowTaskRecordServiceImpl.queryNodeTxBps begin. id:{}", actuatorId);
        List<FlowNodeTxBps> flowNodeTxBpsList = this.getModelMapper().queryNodeTxBps(actuatorId);
        log.info("FlowTaskRecordServiceImpl.queryNodeTxBps end. flowNodeTxBpsList:{}", flowNodeTxBpsList);
        return flowNodeTxBpsList;
    }

    public ArrayNode top5Protocols(String actuatorId) {
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
        return new FlowActuator(flowActuatorModel.getIp(), null).top5Protocols();
    }

    /**
     * 仿真统计
     * @param sceneId
     * @return
     */
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
        List<FlowStatisticsTemp> tempContainOldList = this.getModelMapper()
                .statisticsTaskIdContainOld(sceneId, conditionList);
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
        Integer oldStatus = flowTaskTaskModel.getTaskStatus();
        flowTaskTaskModel.setTaskStatus(status);
        //防止多次推送次数多加，防止手动停止后多加次数
        if (TaskTaskStatusEnum.STOPPED.toValue() == status && TaskTaskStatusEnum.STOPPED.toValue() != oldStatus) {
            flowTaskTaskModel.setSuccessCount(flowTaskTaskModel.getSuccessCount() + 1);
            flowTaskTaskModel.setMsg("");
        } else if (TaskTaskStatusEnum.ERROR.toValue() == status && TaskTaskStatusEnum.ERROR.toValue() != oldStatus) {
            msg = jsonMessage.getString("msg");
            flowTaskTaskModel.setMsg(msg);
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
            flowTaskRecordModel.setRecordStatus(1);
            LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
            flowTaskRecordModel.setCreateTime(localDateTime);
            //flowTaskRecordModel.setCreateTime(StringUtils.isEmpty(data.getString("record_time")) ? null : LocalDateTime.parse(data.getString("record_time"), df));
            this.getModelMapper().save(flowTaskRecordModel);
        }

        // 任务回调处理
        this.flowRecordPostProcess(flowTaskTaskModel, msg);
        log.info("FlowTaskRecordServiceImpl.syncRecord end.");
    }

    /**
     * 任务回调
     * @param flowTaskTaskModel
     * @param msg
     */
    private void flowRecordPostProcess(FlowTaskTaskModel flowTaskTaskModel, String msg) {
        String taskId = flowTaskTaskModel.getId();
        log.info("call task post process for: {}", taskId);
        flowPostService.onFlowTaskOver(flowTaskTaskModel.getSceneId(), flowTaskTaskModel.getNodeId(), flowTaskTaskModel.getFlowId());

        if (StringUtils.isNotBlank(msg)) {
            // 有错误信息，代表执行异常
            flowPostService.onFlowTaskError(flowTaskTaskModel.getSceneId(), flowTaskTaskModel.getNodeId(), flowTaskTaskModel.getFlowId(), msg);
        }

        // 批量执行校验
        String sceneId = flowTaskTaskModel.getSceneId();
        String nodeId = flowTaskTaskModel.getNodeId();
        log.info("batch tasks run for sceneId: {}", sceneId);
        // 判断任务是否都结束
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getSceneId, sceneId);
        condition.filterEqual(FlowTaskTaskModel::getNodeId, nodeId);
        List<FlowTaskTaskModel> taskTaskModels = flowTaskTaskMapper.list(condition);
        if (CollectionUtils.isNotEmpty(taskTaskModels)) {
            long count = 0L;
            for (FlowTaskTaskModel taskModel : taskTaskModels) {
                if (taskModel.getTaskStatus() == TaskTaskStatusEnum.RUNNING.toValue()) {
                    count++;
                }
            }
            int runningCount = (int) count;
            if (runningCount == 0) {
                // 回调参数封装
                BatchActuatorResponse batchActuatorResponse = new BatchActuatorResponse();
                List<FlowTaskModel> successTaskModels = new ArrayList<>();
                List<FlowTaskModel> errorTaskModels = new ArrayList<>();
                for (FlowTaskTaskModel taskTaskModel : taskTaskModels) {
                    FlowTaskModel flowTaskModel = new FlowTaskModel();
                    flowTaskModel.setFlowId(taskTaskModel.getFlowId());
                    flowTaskModel.setId(taskTaskModel.getId());
                    if (TaskTaskStatusEnum.STOPPED.toValue() == taskTaskModel.getTaskStatus()) {
                        successTaskModels.add(flowTaskModel);
                    } else {
                        errorTaskModels.add(flowTaskModel);
                    }
                }
                batchActuatorResponse.setSuccessTaskModels(successTaskModels);
                batchActuatorResponse.setErrorTaskModels(errorTaskModels);

                log.info("batch tasks accept response: {}", batchActuatorResponse);
                flowPostService.onNodeFlowTaskError(sceneId, nodeId, batchActuatorResponse);

                log.info("batch tasks run onStop");
                flowPostService.onStopTaskNode(sceneId, nodeId);
            }
        } else {
            log.error("no batch tasks for sceneId: {}", sceneId);
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
        } else if (totalDiff > 3600 && totalDiff <= 3600 * 2) {
            diff = 10;
            offsetUnit = "m";
        } else if (totalDiff > 3600 * 2 && totalDiff <= 3600 * 5) {
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

    public void batchUpdateStatus(int status, List<String> taskIds) {
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowTaskRecordModel::getTaskId, taskIds.toArray());
        FlowTaskRecordModel recordModel = new FlowTaskRecordModel();
        recordModel.setRecordStatus(0);
        getModelMapper().filterUpdate(recordModel, condition);
    }
}