// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name AsyncPlatFlowTaskService.java.
// File size 66147.
// File create date Fri Jul 12 12:55:31 CST 2024.
// File update date Fri Jul 12 12:55:31 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskFlowConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskIpv4Conf;
import cn.cyberpeace.mp.flow.bo.FlowTaskStart;
import cn.cyberpeace.mp.flow.bo.FlowTaskTcpConf;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.enums.TaskActionEnum;
import cn.cyberpeace.mp.flow.enums.TaskStatusEnum;
import cn.cyberpeace.mp.flow.enums.TaskTaskStatusEnum;
import cn.cyberpeace.mp.flow.integration.actuator.FlowActuator;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.request.BatchStopFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.StopFlowTaskRequest;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskConfService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskRecordService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskTaskService;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import cn.cyberpeace.mp.flow.util.SshUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName AsyncPlatFlowTaskService
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Service
@Slf4j
public class AsyncPlatFlowTaskService {

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    @Autowired
    private FlowTaskTaskService flowTaskTaskService;

    @Autowired
    private FlowTaskConfService flowTaskConfService;

    @Autowired
    private FlowTaskRecordService flowTaskRecordService;

    /**
     * 单个启动流量任务
     * @param taskId
     * @return
     */
    // todo 执行单个流量任务
    public ServiceResult<Boolean> startTask(String taskId) {
        // 任务存在校验
        ObjServiceResult<FlowTaskTaskModel> taskServiceResult = flowTaskTaskService.get(taskId);
        if (Objects.isNull(taskServiceResult.getSource()) || Objects.isNull(taskServiceResult.getSource().getObj())) {
            log.error("task is not exist for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }

        // 任务状态校验
        FlowTaskTaskModel flowTaskTaskModel = taskServiceResult.getSource().getObj();
        if (TaskTaskStatusEnum.RUNNING.toValue() == flowTaskTaskModel.getTaskStatus() || TaskTaskStatusEnum.PREPARING.toValue() == flowTaskTaskModel.getTaskStatus()) {
            log.error("task is running for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_IS_RUNNING);
        }
        if (TaskTaskStatusEnum.INITING.toValue() == flowTaskTaskModel.getTaskStatus()) {
            log.error("task is initing for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_IS_RUNNING);
        }

        // 任务配置校验
        FlowTaskConfModel flowTaskConfModel = flowTaskConfService.findByTaskId(taskId);
        if (Objects.isNull(flowTaskConfModel)) {
            log.error("task config error for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        // 流量执行器校验
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(flowTaskTaskModel.getActuatorId());
        if (Objects.isNull(flowActuatorModel)) {
            log.error("start task error, flowActuator not exist for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        // 将之前流量记录状态设为无效
        flowTaskRecordService.batchUpdateStatus(0, Collections.singletonList(taskId));

        // 解析任务配置
        FlowTaskConf flowTaskConf = JSONObject.parseObject(flowTaskConfModel.getConf(), FlowTaskConf.class);
        // 封装流量任务配置参数
        FlowTaskStart flowTaskStart = FlowTaskStart.builder().taskId(flowTaskTaskModel.getId())
                .flowId(flowTaskTaskModel.getFlowId()).resourceType(flowTaskTaskModel.getResourceType())
                .keepRunning(flowTaskConfModel.getKeepRunning()).commonConf(flowTaskConf.getCommonConf())
                .rateUnlimited(flowTaskConfModel.getRateUnlimited()).rateConf(flowTaskConf.getRateConf())
                .replay(flowTaskConfModel.getReplay()).flowConf(flowTaskConf.getFlowConf())
                .attackConfs(flowTaskConf.getAttackConfs()).build();

        if (flowTaskConf.getRateConf() != null) {
            Integer rate = flowTaskConf.getRateConf().getRate();
            Integer bandwidth = flowTaskConf.getRateConf().getBandwidth();
            if (rate != null || bandwidth != null) {
                flowTaskStart.setRateUnlimited(0);
                flowTaskStart.setKeepRunning(1);
                // 均匀速率
                flowTaskStart.getRateConf().setLoadMode(0);
                flowTaskStart.setReplay(0);
                flowTaskStart.setFlowConf(new FlowTaskFlowConf());
                flowTaskStart.getFlowConf().setTcpConf(new FlowTaskTcpConf());
                flowTaskStart.getFlowConf().setIpv4Conf(new FlowTaskIpv4Conf());
                if (rate == null) {
                    // 速率为空，使用带宽字段
                    flowTaskStart.getRateConf().setRateMin(bandwidth);
                    flowTaskStart.getRateConf().setRateMin(bandwidth);
                } else if (bandwidth == null) {
                    // 带宽为空，使用速率
                    flowTaskStart.getRateConf().setRateMin(rate);
                    flowTaskStart.getRateConf().setRateMax(rate);
                } else {
                    // 都不为空 ， 取小值
                    var smallOne = rate < bandwidth ? rate : bandwidth;
                    flowTaskStart.getRateConf().setRateMax(smallOne);
                    flowTaskStart.getRateConf().setRateMin(smallOne);
                }
            }
        }
        boolean result = new FlowActuator(flowActuatorModel.getIp(), flowTaskTaskService).startTask(taskId, flowTaskStart);
        return new ServiceResult<>(result);
    }

    /**
     * 批量启动流量任务
     * @param taskIds
     * @return
     */
    // todo 批量执行流量任务
    public ServiceResult<Boolean> batchStartTasks(List<String> taskIds) {
        Integer runType = null;

        // 任务存在校验
        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listByTaskIds(taskIds);
        if (CollectionUtils.isEmpty(flowTaskTaskModels) || flowTaskTaskModels.size() != taskIds.size()) {
            log.error("tasks are not all exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }

        // 任务状态校验
        List<String> notRunningTaskIds = new ArrayList<>();
        List<FlowTaskTaskModel> notRunningTasks = new ArrayList<>();
        int PREPARING = 0;
        int INITING = 0 ;
        for (FlowTaskTaskModel taskTaskModel : flowTaskTaskModels) {
            if (TaskTaskStatusEnum.INITING.toValue() == taskTaskModel.getTaskStatus()) {
                INITING++;
            }
            if (TaskTaskStatusEnum.PREPARING.toValue() == taskTaskModel.getTaskStatus()) {
                PREPARING++;
            }
            if (!Arrays.asList(TaskTaskStatusEnum.RUNNING.toValue(), TaskTaskStatusEnum.INITING.toValue(),
                    TaskTaskStatusEnum.PREPARING.toValue(), TaskTaskStatusEnum.INIT.toValue(),
                    TaskTaskStatusEnum.STARTING.toValue()).contains(taskTaskModel.getTaskStatus())){
                notRunningTasks.add(taskTaskModel);
            }
            //if (TaskTaskStatusEnum.RUNNING.toValue() != taskTaskModel.getTaskStatus() && TaskTaskStatusEnum.INITING.toValue() != taskTaskModel.getTaskStatus() && TaskTaskStatusEnum.PREPARING.toValue() != taskTaskModel.getTaskStatus()) {
            //    notRunningTasks.add(taskTaskModel);
            //}
        }
        if (CollectionUtils.isEmpty(notRunningTasks)) {
            if (PREPARING > 0 || INITING > 0) {
                throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("无可运行的流量任务"));
            }
            log.error("some tasks are already running");
            return new ServiceResult<>(true);
        } else {
            List<String> list = new ArrayList<>();
            for (FlowTaskTaskModel notRunningTask : notRunningTasks) {
                String id = notRunningTask.getId();
                list.add(id);
            }
            notRunningTaskIds = list;
            flowTaskTaskModels = flowTaskTaskService.listByTaskIds(notRunningTaskIds);
        }

        // 任务配置校验
        List<FlowTaskConfModel> flowTaskConfModels = flowTaskConfService.listByTaskIds(notRunningTaskIds);
        if (CollectionUtils.isEmpty(flowTaskConfModels) || flowTaskConfModels.size() != notRunningTaskIds.size()) {
            log.error("not all tasks config exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        // 流量执行器校验
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(flowTaskTaskModels.get(0).getActuatorId());
        if (Objects.isNull(flowActuatorModel)) {
            log.error("batch start tasks error, flowActuator not exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        // 批量更新任务之前记录状态为无效
        flowTaskRecordService.batchUpdateStatus(0, notRunningTaskIds);

        // 启动非启动状态的任务
        List<FlowTaskStart> flowTaskStartList = new ArrayList<>();
        for (FlowTaskTaskModel flowTaskTaskModel : flowTaskTaskModels) {// 解析任务配置
            Optional<FlowTaskConfModel> result = Optional.empty();
            for (FlowTaskConfModel taskConfModel : flowTaskConfModels) {
                if (taskConfModel.getTaskId().equals(flowTaskTaskModel.getId())) {
                    result = Optional.of(taskConfModel);
                    break;
                }
            }
            FlowTaskConfModel flowTaskConfModel = result.get();
            FlowTaskConf flowTaskConf = JSONObject.parseObject(flowTaskConfModel.getConf(), FlowTaskConf.class);

            // 封装流量任务配置参数
            FlowTaskStart flowTaskStart = FlowTaskStart.builder().taskId(flowTaskTaskModel.getId())
                    .flowId(flowTaskTaskModel.getFlowId()).resourceType(flowTaskTaskModel.getResourceType())
                    .keepRunning(flowTaskConfModel.getKeepRunning()).commonConf(flowTaskConf.getCommonConf())
                    .rateUnlimited(flowTaskConfModel.getRateUnlimited()).rateConf(flowTaskConf.getRateConf())
                    .replay(flowTaskConfModel.getReplay()).flowConf(flowTaskConf.getFlowConf())
                    .attackConfs(flowTaskConf.getAttackConfs()).build();
            log.info("批量启动流量任务 ， 处理流量任务配置");
            if (flowTaskConf.getRateConf() != null) {
                log.info("批量启动流量任务 ， 处理流量任务配置 ， 保持持续运行流量");
                Integer rate = flowTaskConf.getRateConf().getRate();
                Integer bandwidth = flowTaskConf.getRateConf().getBandwidth();
                if (rate != null || bandwidth != null) {
                    flowTaskStart.setRateUnlimited(0);
                    flowTaskStart.setKeepRunning(1);
                    // 均匀速率
                    flowTaskStart.getRateConf().setLoadMode(0);
                    flowTaskStart.setReplay(0);
                    flowTaskStart.setFlowConf(new FlowTaskFlowConf());
                    flowTaskStart.getFlowConf().setTcpConf(new FlowTaskTcpConf());
                    flowTaskStart.getFlowConf().setIpv4Conf(new FlowTaskIpv4Conf());
                    if (rate == null) {
                        // 速率为空，使用带宽字段
                        flowTaskStart.getRateConf().setRateMin(bandwidth);
                        flowTaskStart.getRateConf().setRateMin(bandwidth);
                    } else if (bandwidth == null) {
                        // 带宽为空，使用速率
                        flowTaskStart.getRateConf().setRateMin(rate);
                        flowTaskStart.getRateConf().setRateMax(rate);
                    } else {
                        // 都不为空 ， 取小值
                        var smallOne = rate < bandwidth ? rate : bandwidth;
                        flowTaskStart.getRateConf().setRateMax(smallOne);
                        flowTaskStart.getRateConf().setRateMin(smallOne);
                    }
                }
            }
            flowTaskStartList.add(flowTaskStart);
        }

        final boolean b = new FlowActuator(flowActuatorModel.getIp(), flowTaskTaskService).startTasks(runType, flowTaskStartList, notRunningTaskIds);
        return new ServiceResult<>(true);
    }

    /**
     * 单个停止流量任务
     * @param taskId
     * @return
     */
    public ServiceResult<Boolean> stopTask(String taskId) {
        log.info("AsyncPlatFlowTaskService.stopTask begin. taskId:{}", taskId);
        // 任务存在校验
        ObjServiceResult<FlowTaskTaskModel> taskServiceResult = flowTaskTaskService.get(taskId);
        if (Objects.isNull(taskServiceResult.getSource()) || Objects.isNull(taskServiceResult.getSource().getObj())) {
            log.error("task is not exist for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }

        // 任务状态校验
        FlowTaskTaskModel flowTaskTaskModel = taskServiceResult.getSource().getObj();
        if (TaskStatusEnum.RUNNING.toValue() != flowTaskTaskModel.getTaskStatus()) {
            log.error("task is not running for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.NO_RUNNING_TASK_TO_STOP);
        }

        // 流量执行器校验
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(flowTaskTaskModel.getActuatorId());
        if (Objects.isNull(flowActuatorModel)) {
            log.error("stop task error, flowActuator not exist for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }
        StopFlowTaskRequest stopFlowTaskRequest = StopFlowTaskRequest.builder().action(TaskActionEnum.STOP.toValue())
                .taskId(taskId).build();

        try {
            String stopTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/v2/tasks/action", JsonUtils.snakeStringify(stopFlowTaskRequest));
            JSONObject stopTaskObj = JSONObject.parseObject(stopTaskRsp);
            log.info("stop task response:{}", stopTaskObj.toJSONString());
            if (ResponseUtils.checkResponse(stopTaskObj)) {
                log.info("stop task success for {}", taskId);

                // 更新任务状态为已停止
                flowTaskTaskModel.setTaskStatus(TaskStatusEnum.STOPED.toValue());
                flowTaskTaskModel.setSuccessCount(flowTaskTaskModel.getSuccessCount() + 1);
                flowTaskTaskService.update(flowTaskTaskModel);
//                flowTaskTaskService.updateTaskStatus(taskId, TaskStatusEnum.STOPED.toValue());
                log.info("AsyncPlatFlowTaskService.stopTask end.");
                return ResultCommon.success(true);
            } else {
                log.error("stop task failed for {}", taskId);
                return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
            }
        } catch (Exception e) {
            log.error("stop task error for {}", taskId, e);
            if (ExceptionUtils.getStackTrace(e).contains("504 Gateway Time-out")) {
                reloadTrex(flowActuatorModel.getIp().split(BaseConstants.COLON)[0]);
            }
            return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }
    }

    /**
     * 批量停止流量任务
     * @param taskIds
     * @return
     */
    public ServiceResult<Boolean> batchStopTasks(List<String> taskIds) {
        log.info("AsyncPlatFlowTaskService.batchStopTasks begin. taskIds:{}", taskIds);
        // 任务存在校验
        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listByTaskIds(taskIds);
        if (CollectionUtils.isEmpty(flowTaskTaskModels) || flowTaskTaskModels.size() != taskIds.size()) {
            log.error("tasks are not all exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }

        // 流量执行器校验
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(flowTaskTaskModels.get(0).getActuatorId());
        if (Objects.isNull(flowActuatorModel)) {
            log.error("batch stop tasks error, flowActuator not exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        List<String> runningTaskIds = new ArrayList<>();
        // 任务状态校验
        List<FlowTaskTaskModel> runningTasks = new ArrayList<>();
        for (FlowTaskTaskModel taskTaskModel : flowTaskTaskModels) {
            if (TaskStatusEnum.RUNNING.toValue() == taskTaskModel.getTaskStatus()) {
                runningTasks.add(taskTaskModel);
            }
        }
        if (CollectionUtils.isEmpty(runningTasks)) {
            log.error("some tasks are not running");
            throw new MpRuntimeException(FlowErrorCode.NO_RUNNING_TASK_TO_STOP);
        } else {
            List<String> list = new ArrayList<>();
            for (FlowTaskTaskModel runningTask : runningTasks) {
                String id = runningTask.getId();
                list.add(id);
            }
            runningTaskIds = list;
        }

        BatchStopFlowTaskRequest batchStopFlowTaskRequest = BatchStopFlowTaskRequest.builder()
                .action(TaskActionEnum.STOP.toValue()).taskIds(taskIds).build();

        try {
            String batchStopTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/v2/tasks/batch/action", JsonUtils.snakeStringify(batchStopFlowTaskRequest));
            JSONObject batchStopTaskObj = JSONObject.parseObject(batchStopTaskRsp);
            log.info("batch stop task response:{}", batchStopTaskObj.toJSONString());
            if (ResponseUtils.checkResponse(batchStopTaskObj)) {
                log.info("batch stop task success");

                // 批量更新任务状态为已停止
                List<FlowTaskTaskModel> runningFlowTaskTaskModels = flowTaskTaskService.listByTaskIds(runningTaskIds);
                for (FlowTaskTaskModel flowTaskTaskModel : runningFlowTaskTaskModels) {
                    flowTaskTaskModel.setTaskStatus(TaskStatusEnum.STOPED.toValue());
                    flowTaskTaskModel.setSuccessCount(flowTaskTaskModel.getSuccessCount() + 1);
                }
                flowTaskTaskService.bulkUpdate(runningFlowTaskTaskModels);
//                flowTaskTaskService.batchUpdateTaskStatus(runningTaskIds, TaskStatusEnum.STOPED.toValue());

                log.info("AsyncPlatFlowTaskService.batchStopTasks end.");
                return ResultCommon.success(true);
            } else {
                log.error("batch stop tasks failed");
                return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
            }
        } catch (Exception e) {
            log.error("batch stop tasks error");
            if (ExceptionUtils.getStackTrace(e).contains("504 Gateway Time-out")) {
                reloadTrex(flowActuatorModel.getIp().split(BaseConstants.COLON)[0]);
            }
            return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }
    }

    /**
     * 重启trex服务
     * @param trexIp
     */
    private void reloadTrex(String trexIp) {
        log.info("reload trex server");
        String command = "python3 /opt/project_dirs/traffic_generators/utils/trex_reload.py";
        SshUtil.ServerInfo serverInfo = new SshUtil.ServerInfo();
        serverInfo.setHost(trexIp);
        serverInfo.setUser("root");
        serverInfo.setPassword("111111");
        SshUtil.execCommand(serverInfo, command);
    }

    /**
     * 异步删除流量文件
     * @param infoModelList
     */
    @Async
    public void deleteFlowFile(List<FlowInfoModel> infoModelList) {
        try {
            if (CollectionUtils.isNotEmpty(infoModelList)) {
                for (FlowInfoModel infoModel : infoModelList) {
                    String filePath = ResourceTypeEnum.SCRIPT.toValue() == infoModel.getResourceType() ? infoModel.getScriptFile() : infoModel.getPcapFile();
                    final boolean delete = FsCommon.delete(filePath);
                }
            }
        } catch (Exception e) {
            log.error("delete flow file error, {}", ExceptionUtils.getStackTrace(e));
        }
    }

}
