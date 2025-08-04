package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.common.TimeCommon;
import cn.cyberpeace.mp.base.constant.BaseSingleton;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.*;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsDetail;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsInfo;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.FlowPlatformEnum;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.enums.TaskStatusEnum;
import cn.cyberpeace.mp.flow.enums.TaskTaskStatusEnum;
import cn.cyberpeace.mp.flow.model.*;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.AddFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.BatchDeleteFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.response.FlowRecordsResponse;
import cn.cyberpeace.mp.flow.service.FlowPortService;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import cn.cyberpeace.mp.flow.service.FlowTaskService;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.flow.service.field.FlowTaskFields;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.HttpDeleteUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 流量任务service
 * @Author qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowTaskServiceImpl extends BaseModelServiceImpl<FlowTaskModel, FlowTaskMapper, FlowTaskFields,
        QueryParams> implements FlowTaskService {

    @Autowired
    private FlowInfoServiceImpl flowInfoService;

    @Autowired
    private FlowExecuteScheduleMethodImpl flowExecuteScheduleMethod;

    @Autowired
    private AsyncPlatFlowTaskService asyncPlatFlowTaskService;

    @Autowired
    private FlowPortService flowPortService;

    @Autowired
    private FlowTaskRecordService flowTaskRecordService;

    @Autowired
    private FlowTaskTaskService flowTaskTaskService;


    public FlowTaskServiceImpl(FlowTaskMapper stuMapper, FlowTaskFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 获取任务详情
     *
     * @param id
     * @param fields
     * @return
     */
    @Deprecated
    public ServiceResult<FlowTaskModel> getDetail(String id, String[] fields) {
        FlowTaskModel flowTaskModel = new FlowTaskModel();
        ObjResult<FlowTaskModel> serviceResult = get(id, fields).getSource();
        if (Objects.nonNull(serviceResult)) {
            flowTaskModel = serviceResult.getObj();

            if (StringUtils.isNotBlank(flowTaskModel.getThirdTaskId()) &&
                    FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform() &&
                    (TaskStatusEnum.RUNNING.toValue() == flowTaskModel.getTaskStatus()) ||
                    TaskStatusEnum.STOPING.toValue() == flowTaskModel.getTaskStatus()) {
                // 当前任务处于运行中或者停止中，重新从流量发生平台获取任务最新状态
                // 流量执行器信息查询
                String actuatorId = flowTaskModel.getActuatorId();
                FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
                if (Objects.isNull(flowActuatorModel)) {
                    return ResultCommon.success(flowTaskModel);
                }

                JSONArray rows = this.queryTaskStatus(Collections.singletonList(flowTaskModel.getThirdTaskId()), CommonUtil.getHostUrl(flowActuatorModel.getIp()));
                if (Objects.nonNull(rows)) {
                    int taskStatus = rows.getJSONObject(0).getInteger("status");
                    flowTaskModel.setTaskStatus(taskStatus);

                    // 更新本地任务状态
                    update(flowTaskModel);
                }
            }
        }

        return ResultCommon.success(flowTaskModel);
    }


    /**
     * 分页查询流量任务信息
     * 针对运行中和停止中的任务，从流量执行平台获取任务最新状态
     *
     * @param queryParams
     * @return
     */
    @Deprecated
    public ListObjResult<FlowTaskModel> pageFlowTask(QueryParams queryParams) {
        ListObjResult<FlowTaskModel> listObjResult = list(queryParams).getSource();
        if (CollectionUtils.isNotEmpty(listObjResult.getList())) {
            // 过滤Trex流量发生平台：状态为执行中和停止中的任务
            List<FlowTaskModel> taskModelList = new ArrayList<>();
            for (FlowTaskModel model : listObjResult.getList()) {
                if (FlowPlatformEnum.TREX.toValue() == model.getFlowPlatform() &&
                        StringUtils.isNotBlank(model.getThirdTaskId()) &&
                        (TaskStatusEnum.RUNNING.toValue() == model.getTaskStatus() ||
                                (TaskStatusEnum.STOPING.toValue() == model.getTaskStatus()))) {
                    taskModelList.add(model);
                }
            }

            if (CollectionUtils.isNotEmpty(taskModelList)) {
                // 流量执行器信息查询
                String actuatorId = taskModelList.get(0).getActuatorId();
                FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
                if (Objects.isNull(flowActuatorModel)) {
                    return listObjResult;
                }

                List<String> thirdTaskIds =
                        new ArrayList<>();
                for (FlowTaskModel flowTaskModel : taskModelList) {
                    String thirdTaskId = flowTaskModel.getThirdTaskId();
                    thirdTaskIds.add(thirdTaskId);
                }

                JSONArray rows = this.queryTaskStatus(thirdTaskIds, CommonUtil.getHostUrl(flowActuatorModel.getIp()));
                if (Objects.nonNull(rows)) {
                    for (FlowTaskModel task : taskModelList) {
                        for (int i = 0; i < rows.size(); i++) {
                            JSONObject obj = rows.getJSONObject(i);
                            if (task.getThirdTaskId().equals(obj.getString("uuid"))) {
                                // 更新任务状态
                                task.setTaskStatus(obj.getInteger("status"));
                            }
                        }
                    }

                    // 批量更新本地任务状态
                    bulkUpdate(taskModelList);
                } else {
                    log.error("query task status failed");
                }
            }
        }

        return listObjResult.asResult();
    }

    /**
     * 新增流量任务
     *
     * @param request
     * @return
     */
    public ServiceResult<FlowTaskModel> addFlowTask(AddFlowTaskRequest request) {
        if (StringUtils.isBlank(request.getOriginIps())) {
            // 设置默认来源ip
            request.setOriginIps("127.0.0.1-127.0.0.1");
        }

        FlowTaskModel flowTaskModel = request;
        // 是否自定义流量
        Boolean isCustom = StringUtils.isNotBlank(flowTaskModel.getFlowProtocol());

        if (isCustom) {
            // 自定义流量，没有流量文件
            create(flowTaskModel);
            // 异步新增流量发生平台流量任务
            asyncPlatFlowTaskService.savePlatFlowTask(request, false);
            return ResultCommon.success(flowTaskModel);
        } else {
            ObjResult<FlowInfoModel> flowInfoObjResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
            if (Objects.nonNull(flowInfoObjResult)) {
                FlowInfoModel flowInfoModel = flowInfoObjResult.asResult();

                if (FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform()) {
                    if (asyncPlatFlowTaskService.uploadFlowInfo(flowInfoModel, flowTaskModel, request)) {
                        create(flowTaskModel);

                        // 异步新增流量发生平台流量任务
                        asyncPlatFlowTaskService.savePlatFlowTask(request, false);
                        return ResultCommon.success(flowTaskModel);
                    } else {
                        return new ServiceResult<>(null, FlowErrorCode.ADD_TASK_ERROR);
                    }
                } else {
                    // 暂无其他流量发生器，直接保存
                    create(flowTaskModel);
                    return ResultCommon.success(flowTaskModel);
                }
            } else {
                log.error("flow file be chosen not existed");
                return new ServiceResult<>(null, FlowErrorCode.ADD_TASK_ERROR);
            }
        }
    }


    /**
     * 更新流量任务
     *
     * @param request
     * @return
     */
    @Deprecated
    public ServiceResult<FlowTaskModel> updateFlowTask(AddFlowTaskRequest request) {
        if (StringUtils.isBlank(request.getOriginIps())) {
            // 设置默认来源ip
            request.setOriginIps("127.0.0.1-127.0.0.1");
        }
        FlowTaskModel flowTaskModel = request;
        ObjResult<FlowTaskModel> flowTaskResult = this.get(flowTaskModel.getId()).getSource();
        if (Objects.isNull(flowTaskResult)) {
            log.error("flow task not exist");
            return ResultCommon.fail(FlowErrorCode.TASK_NOT_EXIST);
        }
        FlowTaskModel oldTaskModel = flowTaskResult.asResult();
        // 查询流量执行器地址
        String actuatorId = flowTaskModel.getActuatorId();
        FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
        String resourceUrl = CommonUtil.getResourcetUrl(flowActuatorModel.getIp());

        ObjResult<FlowInfoModel> oldFlowInfoResult = flowInfoService.get(oldTaskModel.getFlowId()).getSource();

        if (FlowPlatformEnum.TREX.toValue() == oldTaskModel.getFlowPlatform()) {
            if (Objects.nonNull(oldFlowInfoResult)) {
                FlowInfoModel oldFlowInfoModel = oldFlowInfoResult.asResult();
                // 判断是否更新了流量文件
                if (!oldTaskModel.getFlowId().equals(flowTaskModel.getFlowId())) {
                    // 更新了流量文件，需要删除流量发生平台原流量文件
                    if (!this.deleteFlowFile(oldFlowInfoModel.getResourceType(),
                            Collections.singletonList(oldTaskModel.getThirdFlowId()), resourceUrl)) {
                        log.warn("delete old flow file error");
                    }

                    // 再向流量发生平台上传新流量文件
                    ObjResult<FlowInfoModel> newFlowInfoResult =
                            flowInfoService.get(flowTaskModel.getFlowId()).getSource();
                    if (Objects.nonNull(newFlowInfoResult)) {
                        FlowInfoModel newFlowInfoModel = newFlowInfoResult.asResult();
                        if (!asyncPlatFlowTaskService.uploadFlowInfo(newFlowInfoModel, flowTaskModel, request)) {
                            return ResultCommon.fail(FlowErrorCode.UPDATE_TASK_ERROR);
                        }
                    } else {
                        log.error("flow info not exist:{}", flowTaskModel.getFlowId());
                        return ResultCommon.fail(FlowErrorCode.UPDATE_TASK_ERROR);
                    }
                } else {
                    log.info("no need to update flow file");
                }
            } else {
                log.warn("old flow file not exist");
                // 直接向流量发生平台上传新流量文件
                ObjResult<FlowInfoModel> newFlowInfoResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
                if (Objects.nonNull(newFlowInfoResult)) {
                    FlowInfoModel newFlowInfoModel = newFlowInfoResult.asResult();
                    if (!asyncPlatFlowTaskService.uploadFlowInfo(newFlowInfoModel, flowTaskModel, request)) {
                        return ResultCommon.fail(FlowErrorCode.UPDATE_TASK_ERROR);
                    }
                } else {
                    log.error("flow info not exist:{}", flowTaskModel.getFlowId());
                    return ResultCommon.fail(FlowErrorCode.UPDATE_TASK_ERROR);
                }
            }

            // 异步更新流量发生平台任务
            asyncPlatFlowTaskService.savePlatFlowTask(request, true);
        }

        // 更新本地信息
        update(flowTaskModel);
        return ResultCommon.success(flowTaskModel);
    }

    /**
     * 删除流量任务
     *
     * @param id 流量任务id
     * @return
     */
    public ServiceResult<Boolean> deleteFlowTask(String id) {
        ObjResult<FlowTaskModel> flowTaskResult = get(id).getSource();
        if (Objects.nonNull(flowTaskResult)) {
            FlowTaskModel flowTaskModel = flowTaskResult.asResult();

            ObjResult<FlowInfoModel> flowInfoResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
            if (Objects.nonNull(flowInfoResult)) {
                if (FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform()) {
                    // 查询流量执行器地址
                    String actuatorId = flowTaskModel.getActuatorId();
                    FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
                    String resourceUrl = CommonUtil.getResourcetUrl(flowActuatorModel.getIp());

                    FlowInfoModel oldModel = flowInfoResult.asResult();
                    // 删除流量任务，同时需要删除该任务关联的流量发生平台流量文件
                    if (this.deleteFlowFile(oldModel.getResourceType(),
                            Collections.singletonList(flowTaskModel.getThirdFlowId()), resourceUrl)) {
                        log.info("delete flow file success");
                    } else {
                        log.error("delete flow file error");
                    }

                    // 异步删除流量平台任务
                    asyncPlatFlowTaskService.deletePlatFlowTask(oldModel.getResourceType(), Collections.singletonList(flowTaskModel));
                }
            } else {
                log.warn("delete flow file not exit");
            }

            return delete(id).asResult();
        } else {
            log.error("flow task not exist: {}", id);
            return ResultCommon.fail(FlowErrorCode.TASK_NOT_EXIST);
        }

    }

    /**
     * 批量删除流量任务
     *
     * @param request 批量删除请求
     * @return
     */
    @Deprecated
    public ServiceResult<Integer> batchDeleteFlowTask(BatchDeleteFlowTaskRequest request) {
        QueryParams taskParams = new QueryParams();
        String[] taskDelIds = request.getIdList().toArray(new String[0]);
        taskParams.setIds(taskDelIds);
        taskParams.setPage(BaseConstants.DEFAULT_PAGE);
        taskParams.setPageSize(BaseSingleton.baseAppConfig.getMaxPageSize());
        ListObjResult<FlowTaskModel> flowTaskResult = list(taskParams).getSource();
        List<FlowTaskModel> flowTaskList = flowTaskResult.getList();

        List<FlowTaskModel> pcapTaskModels = new ArrayList<>();
        List<FlowTaskModel> scriptTaskModels = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(flowTaskList)) {
            FlowInfoQueryParams flowInfoParams = new FlowInfoQueryParams();
            List<String> list1 = new ArrayList<>();
            for (FlowTaskModel taskModel : flowTaskList) {
                String flowId = taskModel.getFlowId();
                list1.add(flowId);
            }
            String[] flowInfoDelIds = list1.toArray(new String[0]);
            flowInfoParams.setIds(flowInfoDelIds);
            flowInfoParams.setPage(BaseConstants.DEFAULT_PAGE);
            flowInfoParams.setPageSize(BaseSingleton.baseAppConfig.getMaxPageSize());
            ListObjServiceResult<FlowInfoModel> flowInfoResult = flowInfoService.list(flowInfoParams);
            List<FlowInfoModel> infoModelList = flowInfoResult.getSource().getObjs();

            if (CollectionUtils.isNotEmpty(infoModelList)) {
                // 过滤出所有pcap数据
                List<String> pcapIdList =
                        new ArrayList<>();
                for (FlowInfoModel flowInfoModel : infoModelList) {
                    if ((ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType())) {
                        String flowInfoModelId = flowInfoModel.getId();
                        pcapIdList.add(flowInfoModelId);
                    }
                }
                List<FlowTaskModel> result = new ArrayList<>();
                for (FlowTaskModel flowTaskModel : flowTaskList) {
                    if (FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform() &&
                            pcapIdList.contains(flowTaskModel.getFlowId())) {
                        result.add(flowTaskModel);
                    }
                }
                pcapTaskModels = result;

                // 过滤出所有script数据
                List<String> scriptIdList =
                        new ArrayList<>();
                for (FlowInfoModel model : infoModelList) {
                    if ((ResourceTypeEnum.SCRIPT.toValue() == model.getResourceType())) {
                        String id = model.getId();
                        scriptIdList.add(id);
                    }
                }
                List<FlowTaskModel> list = new ArrayList<>();
                for (FlowTaskModel task : flowTaskList) {
                    if (FlowPlatformEnum.TREX.toValue() == task.getFlowPlatform() &&
                            scriptIdList.contains(task.getFlowId())) {
                        list.add(task);
                    }
                }
                scriptTaskModels = list;
            } else {
                log.warn("no flow file to delete");
            }
        } else {
            log.error("no flow task to delete");
            return ResultCommon.fail(FlowErrorCode.NO_TASK_TO_DELETE);
        }

        ServiceResult<Integer> buldDelResult = bulkDelete(taskDelIds);

        // 异步删除流量平台任务
        if (CollectionUtils.isNotEmpty(pcapTaskModels)) {
            asyncPlatFlowTaskService.deletePlatFlowTask(ResourceTypeEnum.PACP.toValue(), pcapTaskModels);
        }
        if (CollectionUtils.isNotEmpty(scriptTaskModels)) {
            asyncPlatFlowTaskService.deletePlatFlowTask(ResourceTypeEnum.SCRIPT.toValue(), scriptTaskModels);
        }

        return buldDelResult;
    }

    /**
     * 批量删除流量发生平台文件
     *
     * @param resourceType
     * @param thirdFlowIds
     * @return
     */
    private boolean deleteFlowFile(int resourceType, List<String> thirdFlowIds, String resourceUrl) {
        String delUrl = "";
        if (ResourceTypeEnum.PACP.toValue() == resourceType) {
            log.info("start to delete pcap");
            // 删除pcap包地址
            delUrl = "/pcap";
        } else {
            log.info("start to delete scripts");
            // 删除脚本文件地址
            delUrl = "/scripts";
        }

        if (CollectionUtils.isEmpty(thirdFlowIds)) {
            log.info("no thirdFlowIds to delete");
            return true;
        }

        JSONObject object = new JSONObject();
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(thirdFlowIds));
        object.put("uuids", array);

        try {
            String rsp = HttpDeleteUtil.sendDelete(resourceUrl + delUrl, object.toJSONString());
            JSONObject rspObject = JSONObject.parseObject(rsp);
            log.info("delete flow file response: {}", rspObject.toJSONString());

            if (ResponseUtils.checkResponse(rspObject)) {
                return true;
            } else {
                log.error("delete flow file response error");
            }
        } catch (Exception e) {
            log.error("delete file from flow actuator platform error, {}", ExceptionUtils.getStackTrace(e));
        }

        return false;
    }

    /**
     * 查询流量执行记录
     * @param taskId
     * @return
     */
    @Deprecated
    public ServiceResult<FlowRecordsResponse> queryFlowRecords(String taskId) {
        // 批量任务传的是nodeId，直接用该值查询
        String thirdTaskId = null;
        FlowRecordsResponse response = new FlowRecordsResponse();
        FlowTaskModel flowTaskModel = getModelMapper().get(taskId);
        if (Objects.isNull(flowTaskModel)) {
            taskId = "MP_SCENE_" + taskId;
            thirdTaskId = taskId;
            HumanCondition condition = new HumanCondition().filterEqual(FlowTaskModel::getNodeId, taskId);
            List<FlowTaskModel> flowTaskModelList = getModelMapper().list(condition);
            if (CollectionUtils.isEmpty(flowTaskModelList)) {
                log.warn("task info is empty for taskId:{}", taskId);
                // 流量任务不存在
                return ResultCommon.success(new FlowRecordsResponse(null, null, new ArrayList<>()));
            } else {
                flowTaskModel = flowTaskModelList.get(0);
            }
        } else {
            // 单个任务用thirdTaskId
            thirdTaskId = flowTaskModel.getThirdTaskId();
        }

        // 流量执行器信息查询
        String actuatorId = flowTaskModel.getActuatorId();
        FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            log.error("flowActuatorModel is empty for actuatorId:{}", actuatorId);
            // 流量任务异常
            throw new MpRuntimeException(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }

        JSONArray rows = this.queryTaskStatus(Collections.singletonList(thirdTaskId), CommonUtil.getHostUrl(flowActuatorModel.getIp()));
        if (CollectionUtils.isNotEmpty(rows)) {
            List<FlowRecords> flowRecordsList = new ArrayList<>();
            JSONArray flowRecordsArray = rows.getJSONObject(0).getJSONArray("flow_records");
            // 流量运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过
            Integer status = rows.getJSONObject(0).getInteger("status");
            // 流量运行结果：1 成功， 2 失败
            Integer result = rows.getJSONObject(0).getInteger("result");

            try {
                if (Objects.nonNull(flowRecordsArray)) {
                    flowRecordsList = flowRecordsArray.toJavaList(FlowRecords.class);
                }
            } catch (Exception e) {
                log.error("queryFlowRecords parse result error:{}", ExceptionUtils.getStackTrace(e));
            }
            response.setFlowRecordsList(flowRecordsList);
            response.setStatus(status);
            response.setResult(result);
        }

        return ResultCommon.success(response);
    }

    /**
     * 任务单个运行结果查询
     * @param taskId
     * @return
     */
    public ServiceResult<FlowRecordsResponse> queryFlowRecordsByTaskId(String taskId) {
        FlowRecordsResponse response = new FlowRecordsResponse();
        List<FlowRecords> flowRecordsList = new ArrayList<>();
        response.setFlowRecordsList(flowRecordsList);

        List<FlowTaskRecordModel> flowTaskRecordModels = flowTaskRecordService.findByTaskId(taskId);
        for (int i = 0; i < flowTaskRecordModels.size(); i++) {
            FlowTaskRecordModel flowTaskRecordModel = flowTaskRecordModels.get(i);
            FlowRecords flowRecords = new FlowRecords();
            flowRecords.setTaskUuid(taskId);
            flowRecords.setId(i + 1);
            flowRecords.setSecond(i + 1);
            flowRecords.setTxBps(Objects.nonNull(flowTaskRecordModel.getTxBps()) ? flowTaskRecordModel.getTxBps().doubleValue() : 0);
            flowRecords.setOpackets(Objects.nonNull(flowTaskRecordModel.getTotalPps()) ? flowTaskRecordModel.getTotalPps() : 0);
            flowRecords.setObytes(Objects.nonNull(flowTaskRecordModel.getObytes()) ? flowTaskRecordModel.getObytes() : 0);
            flowRecords.setOerrors(Objects.nonNull(flowTaskRecordModel.getOerrors()) ? flowTaskRecordModel.getOerrors().intValue() : 0);
            flowRecords.setCreateTime(TimeCommon.localDateTimeToString(flowTaskRecordModel.getCreateTime()));
            flowRecordsList.add(flowRecords);
        }

        ObjServiceResult<FlowTaskTaskModel> objServiceResult = flowTaskTaskService.get(taskId);
        if(objServiceResult.sourceObj() != null){
            FlowTaskTaskModel task = objServiceResult.sourceObj();
            response.setStatus(task.getTaskStatus());
            response.setResult(task.getErrorCount()>0?2:1);
        }
        return ResultCommon.success(response);
    }

    /**
     * 任务批量运行结果查询
     * @param sceneId
     * @return
     */
    public ServiceResult<FlowRecordsResponse> queryBatchFlowRecords(String sceneId) {
        FlowRecordsResponse response = new FlowRecordsResponse();
        List<FlowRecords> flowRecordsList = new ArrayList<>();
        response.setFlowRecordsList(flowRecordsList);

        FlowStatisticsInfo flowStatisticsInfo = flowTaskRecordService.flowStatistics(sceneId);
        List<FlowStatisticsDetail> txBpsList = flowStatisticsInfo.getTxBpsList();
        if (CollectionUtils.isNotEmpty(txBpsList)) {
            for (int i = 0; i < txBpsList.size(); i++) {
                FlowStatisticsDetail flowStatisticsDetail = txBpsList.get(i);
                FlowRecords flowRecords = new FlowRecords();
                flowRecords.setId(i + 1);
                flowRecords.setSecond(i + 1);
                flowRecords.setTxBps(Objects.nonNull(flowStatisticsDetail.getValue()) ? flowStatisticsDetail.getValue().doubleValue() : 0);
                flowRecords.setOpackets(Objects.nonNull(flowStatisticsInfo.getTotalPps()) ? flowStatisticsInfo.getTotalPps() : 0);
                flowRecords.setObytes(Objects.nonNull(flowStatisticsInfo.getObytes()) ? flowStatisticsInfo.getObytes() : 0);
                flowRecords.setOerrors(Objects.nonNull(flowStatisticsInfo.getOerrors()) ? flowStatisticsInfo.getOerrors().intValue() : 0);
                flowRecordsList.add(flowRecords);
            }
        }

        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listBySceneIdAndStatus(sceneId,
                TaskTaskStatusEnum.RUNNING.toValue());
        if (CollectionUtils.isNotEmpty(flowTaskTaskModels)) {
            response.setStatus(TaskTaskStatusEnum.RUNNING.toValue());
        } else {
            response.setStatus(TaskTaskStatusEnum.STOPPED.toValue());
        }
        response.setResult(1);

        return ResultCommon.success(response);
    }

    /**
     * 查询流量任务状态
     *
     * @param thirdTaskIds
     * @param hostUrl
     * @return
     */
    private JSONArray queryTaskStatus(List<String> thirdTaskIds, String hostUrl) {
        JSONArray rows = null;
        if (CollectionUtils.isEmpty(thirdTaskIds)) {
            log.warn("no thirdTaskIds to query");
            return rows;
        }

        if (StringUtils.isBlank(hostUrl)) {
            log.error("hostUrl is empty");
            return rows;
        }

        try {
            JSONObject statusObj = new JSONObject();
            JSONArray statusArray = new JSONArray();
            statusArray.addAll(thirdTaskIds);
            statusObj.put("uuids", statusArray);

            String statusRsp = HttpUtils.sendPost(hostUrl + "/tasks/standards/many",
                    statusObj.toJSONString());
            JSONObject statusRspObj = JSONObject.parseObject(statusRsp);
            log.info("query task status response:{}", statusRspObj.toJSONString());
            if (ResponseUtils.checkResponse(statusRspObj)) {
                rows = statusRspObj.getJSONObject(BaseConstants.STR_DATA).getJSONArray("rows");
            } else {
                log.error("query task status failed");
            }
        } catch (Exception e) {
            log.error("query task status error, {}", ExceptionUtils.getStackTrace(e));
        }

        return rows;
    }

    /**
     * 增加实际运行次数
     * @param taskId
     * @return
     */
    public int increaseRunningCount(String taskId) {
        return getModelMapper().increaseRunningCount(taskId);
    }

    /**
     * 批量更新流量任务信息
     * @param taskStatus
     * @param runCount
     * @param taskIds
     * @return
     */
    @Deprecated
    public int batchUpdateTaskStatus(Integer taskStatus, Boolean runCount, List<String> taskIds) {
        return getModelMapper().batchUpdateTaskStatus(taskStatus, runCount, taskIds);
    }

    /**
     * 更新流量任务
     * @param flowTaskModel
     * @return
     */
    public int updateFlowTaskInfo(FlowTaskModel flowTaskModel) {
        if (StringUtils.isNotBlank(flowTaskModel.getOriginIps())) {
            // 更新来源ip时，封装成范围格式
            String[] srcIps = flowTaskModel.getOriginIps().split(BaseConstants.SHORT_DASH);
            String srcIpRange;
            if (srcIps.length < 2) {
                srcIpRange = srcIps[0] + BaseConstants.SHORT_DASH + srcIps[0];
                flowTaskModel.setOriginIps(srcIpRange);
            }
        } else {
            flowTaskModel.setOriginIps("127.0.0.1-127.0.0.1");
        }
        int result = getModelMapper().updateTaskInfo(flowTaskModel);
        flowTaskModel = getModelMapper().get(flowTaskModel.getId());
        if (Objects.isNull(flowTaskModel)) {
            return 0;
        }
        AddFlowTaskRequest addFlowTaskRequest = new AddFlowTaskRequest();
        BeanUtils.copyProperties(flowTaskModel, addFlowTaskRequest);
        asyncPlatFlowTaskService.savePlatFlowTask(addFlowTaskRequest, true);
        return result;
    }

    /**
     * 更新流量任务
     * @param flowTaskModel
     * @return
     */
    @Deprecated
    public int updateFlowTaskDetail(FlowTaskModel flowTaskModel) {
        return getModelMapper().updateTaskInfo(flowTaskModel);
    }


    /**
     * 批量新增流量任务
     * @param addTaskRequestList
     * @return
     */
    public List<FlowTaskModel> batchSavePlatFlowTask(List<AddFlowTaskRequest> addTaskRequestList) {
        return asyncPlatFlowTaskService.batchSavePlatFlowTask(addTaskRequestList);
    }
}
