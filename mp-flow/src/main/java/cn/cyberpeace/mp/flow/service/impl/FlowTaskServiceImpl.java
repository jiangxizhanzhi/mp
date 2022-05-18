package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.common.TimeCommon;
import cn.cyberpeace.mp.base.constant.BaseSingleton;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.*;
import cn.cyberpeace.mp.flow.model.*;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.*;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.response.FlowRecordsResponse;
import cn.cyberpeace.mp.flow.service.FlowPortService;
import cn.cyberpeace.mp.flow.service.FlowTaskService;
import cn.cyberpeace.mp.flow.service.field.FlowTaskFields;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.FlowExecuteScheduleUtil;
import cn.cyberpeace.mp.flow.util.HttpDeleteUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                    return ServiceResult.success(flowTaskModel);
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

        return ServiceResult.success(flowTaskModel);
    }


    /**
     * 分页查询流量任务信息
     * 针对运行中和停止中的任务，从流量执行平台获取任务最新状态
     *
     * @param queryParams
     * @return
     */
    public ListObjResult<FlowTaskModel> pageFlowTask(QueryParams queryParams) {
        ListObjResult<FlowTaskModel> listObjResult = list(queryParams).getSource();
        if (CollectionUtils.isNotEmpty(listObjResult.getList())) {
            // 过滤Trex流量发生平台：状态为执行中和停止中的任务
            List<FlowTaskModel> taskModelList = listObjResult.getList().stream().filter(model ->
                    FlowPlatformEnum.TREX.toValue() == model.getFlowPlatform() &&
                            StringUtils.isNotBlank(model.getThirdTaskId()) &&
                            (TaskStatusEnum.RUNNING.toValue() == model.getTaskStatus() ||
                                    (TaskStatusEnum.STOPING.toValue() == model.getTaskStatus())))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(taskModelList)) {
                // 流量执行器信息查询
                String actuatorId = taskModelList.get(0).getActuatorId();
                FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
                if (Objects.isNull(flowActuatorModel)) {
                    return listObjResult;
                }

                List<String> thirdTaskIds =
                        taskModelList.stream().map(FlowTaskModel::getThirdTaskId).collect(Collectors.toList());

                JSONArray rows = this.queryTaskStatus(thirdTaskIds, CommonUtil.getHostUrl(flowActuatorModel.getIp()));
                if (Objects.nonNull(rows)) {
                    taskModelList.forEach(task -> {
                        for (int i = 0; i < rows.size(); i++) {
                            JSONObject obj = rows.getJSONObject(i);
                            if (task.getThirdTaskId().equals(obj.getString("uuid"))) {
                                // 更新任务状态
                                task.setTaskStatus(obj.getInteger("status"));
                            }
                        }
                    });

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
        log.info("add flow task request:{}", request.toString());

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
            return ServiceResult.success(flowTaskModel);
        } else {
            ObjResult<FlowInfoModel> flowInfoObjResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
            if (Objects.nonNull(flowInfoObjResult)) {
                FlowInfoModel flowInfoModel = flowInfoObjResult.asResult();

                if (FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform()) {
                    if (asyncPlatFlowTaskService.uploadFlowInfo(flowInfoModel, flowTaskModel, request)) {
                        create(flowTaskModel);

                        // 异步新增流量发生平台流量任务
                        asyncPlatFlowTaskService.savePlatFlowTask(request, false);
                        return ServiceResult.success(flowTaskModel);
                    } else {
                        return new ServiceResult<>(null, FlowErrorCode.ADD_TASK_ERROR);
                    }
                } else {
                    // 暂无其他流量发生器，直接保存
                    create(flowTaskModel);
                    return ServiceResult.success(flowTaskModel);
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
    public ServiceResult<FlowTaskModel> updateFlowTask(AddFlowTaskRequest request) {
        if (StringUtils.isBlank(request.getOriginIps())) {
            // 设置默认来源ip
            request.setOriginIps("127.0.0.1-127.0.0.1");
        }
        FlowTaskModel flowTaskModel = request;
        ObjResult<FlowTaskModel> flowTaskResult = this.get(flowTaskModel.getId()).getSource();
        if (Objects.isNull(flowTaskResult)) {
            log.error("flow task not exist");
            return ServiceResult.fail(FlowErrorCode.TASK_NOT_EXIST);
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
                            return ServiceResult.fail(FlowErrorCode.UPDATE_TASK_ERROR);
                        }
                    } else {
                        log.error("flow info not exist:{}", flowTaskModel.getFlowId());
                        return ServiceResult.fail(FlowErrorCode.UPDATE_TASK_ERROR);
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
                        return ServiceResult.fail(FlowErrorCode.UPDATE_TASK_ERROR);
                    }
                } else {
                    log.error("flow info not exist:{}", flowTaskModel.getFlowId());
                    return ServiceResult.fail(FlowErrorCode.UPDATE_TASK_ERROR);
                }
            }

            // 异步更新流量发生平台任务
            asyncPlatFlowTaskService.savePlatFlowTask(request, true);
        }

        // 更新本地信息
        update(flowTaskModel);
        return ServiceResult.success(flowTaskModel);
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
            return ServiceResult.fail(FlowErrorCode.TASK_NOT_EXIST);
        }

    }

    /**
     * 批量删除流量任务
     *
     * @param request 批量删除请求
     * @return
     */
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
            String[] flowInfoDelIds = flowTaskList.stream().map(FlowTaskModel::getFlowId).toArray(String[]::new);
            flowInfoParams.setIds(flowInfoDelIds);
            flowInfoParams.setPage(BaseConstants.DEFAULT_PAGE);
            flowInfoParams.setPageSize(BaseSingleton.baseAppConfig.getMaxPageSize());
            ListObjServiceResult<FlowInfoModel> flowInfoResult = flowInfoService.list(flowInfoParams);
            List<FlowInfoModel> infoModelList = flowInfoResult.getSource().getObjs();

            if (CollectionUtils.isNotEmpty(infoModelList)) {
                // 过滤出所有pcap数据
                List<String> pcapIdList =
                        infoModelList.stream().filter(model -> (ResourceTypeEnum.PACP.toValue() == model.getResourceType())).map(FlowInfoModel::getId).collect(Collectors.toList());
                pcapTaskModels = flowTaskList.stream().filter(task ->
                                FlowPlatformEnum.TREX.toValue() == task.getFlowPlatform() &&
                                        pcapIdList.contains(task.getFlowId())).collect(Collectors.toList());

                // 过滤出所有script数据
                List<String> scriptIdList =
                        infoModelList.stream().filter(model -> (ResourceTypeEnum.SCRIPT.toValue() == model.getResourceType())).map(FlowInfoModel::getId).collect(Collectors.toList());
                scriptTaskModels = flowTaskList.stream().filter(task ->
                                FlowPlatformEnum.TREX.toValue() == task.getFlowPlatform() &&
                                        scriptIdList.contains(task.getFlowId())).collect(Collectors.toList());
            } else {
                log.warn("no flow file to delete");
            }
        } else {
            log.error("no flow task to delete");
            return ServiceResult.fail(FlowErrorCode.NO_TASK_TO_DELETE);
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
            log.error("delete file from flow actuator platform error");
        }

        return false;
    }


    /**
     * 执行流量任务
     *
     * @param flowTaskId
     * @param taskAction 1 start， 2 stop, 3 reset
     * @param callback 回调方法
     * @param errorCallback 执行错误时回调方法
     * @return
     */
    public ServiceResult<Boolean> actuator(String flowTaskId, int taskAction, Runnable callback, Runnable errorCallback) {
        log.info("=========== Start actuator flow:[{}] ===============", flowTaskId);
        // 流量任务存在校验
        ObjResult<FlowTaskModel> flowTaskResult = get(flowTaskId).getSource();
        if (Objects.isNull(flowTaskResult)) {
            log.error("flow task not exist:{}", flowTaskId);
            return ServiceResult.fail(FlowErrorCode.TASK_NOT_EXIST);
        }
        FlowTaskModel flowTaskModel = flowTaskResult.getObj();

        // 判断调用哪个流量执行器
        if (FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform()) {
            log.info("=========== Actuator trex flow end ===============");
            return this.actuatorTrex(flowTaskModel, taskAction, callback, errorCallback);
        } else {
            log.info("=========== Actuator other flow end ===============");
            return ServiceResult.fail(FlowErrorCode.NO_ACTUATOR_TO_USE);
        }
    }

    /**
     * 调用Trex流量执行器
     * @param flowTaskModel
     * @param taskAction    1 start， 2 stop, 3 reset
     * @param callback  流量停止回调方法
     * @param errorCallback 流量执行异常回调方法
     * @return
     */
    private ServiceResult<Boolean> actuatorTrex(FlowTaskModel flowTaskModel, int taskAction, Runnable callback, Runnable errorCallback) {
        FlowInfoModel flowInfoModel = new FlowInfoModel();
        // 是否自定义流量
        boolean isCustom = false;
        if (Objects.equals(CustomFlagEnum.NO.toValue(), flowTaskModel.getCustomFlag())) {
            // 流量信息存在校验
            ObjResult<FlowInfoModel> flowInfoResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
            if (Objects.isNull(flowInfoResult)) {
                log.error("flow info in task not exist:{}", flowTaskModel.getFlowId());
                return ServiceResult.fail(FlowErrorCode.FLOW_IN_TASK_NOT_EXIST);
            }
            flowInfoModel = flowInfoResult.getObj();
        } else {
            isCustom = true;
            flowInfoModel.setResourceType(ResourceTypeEnum.PACP.toValue());
        }

        // 流量执行器信息查询
        String actuatorId = flowTaskModel.getActuatorId();
        FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            return ServiceResult.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }

        ObjResult<FlowPortModel> portObjResult = flowPortService.get(flowTaskModel.getFlowPortId()).getSource();
        if (Objects.isNull(portObjResult)) {
            if (Objects.equals(CustomFlagEnum.YES.toValue(), flowTaskModel.getCustomFlag())) {
                log.error("custom flow can only run once");
                return ServiceResult.fail(FlowErrorCode.RECREATE_CUSTOM_FLOW_ERROR);
            } else {
                log.error("flow port info is empty");
                return ServiceResult.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
            }
        }
        FlowPortModel flowPortModel = portObjResult.asResult();

        String thirdTaskId = flowTaskModel.getThirdTaskId();
        if (StringUtils.isNotBlank(thirdTaskId)) {
            // 流量发生平台已经创建过任务，并且是启动任务，需要判断当前任务状态
            if (TaskActionEnum.START.toValue() == taskAction) {
                JSONArray rows = this.queryTaskStatus(Collections.singletonList(thirdTaskId), CommonUtil.getHostUrl(flowActuatorModel.getIp()));
                if (Objects.nonNull(rows)) {
                    int taskStatus = rows.getJSONObject(0).getInteger("status");
                    if (TaskStatusEnum.RUNNING.toValue() == taskStatus || TaskStatusEnum.STOPING.toValue() == taskStatus) {
                        return ServiceResult.fail(FlowErrorCode.TASK_IS_RUNNING);
                    }
                } else {
                    log.error("query task status failed");
                    return ServiceResult.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
                }
            }
        } else {
            // 流量平台未创建过任务，并且是停止任务，提示错误
            if (TaskActionEnum.STOP.toValue() == taskAction) {
                log.error("third task not exist");
                return ServiceResult.fail(FlowErrorCode.NO_RUNNING_TASK_TO_STOP);
            }
        }

        // 执行任务地址
        String urlPath = "";
        if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
            if (isCustom) {
                // 自定义流量
                urlPath = "/tasks/custom/action";
            } else {
                // 背景流量
                urlPath = "/tasks/action";
            }

            // 判断是否关联流量平台任务，没有则新增
            if (StringUtils.isBlank(thirdTaskId)) {
                // 构建新增app任务请求
                AddAppTaskRequest appTaskRequest = new AddAppTaskRequest(flowTaskModel, flowPortModel);
                try {
                    String addAppTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/app",
                            JsonUtils.snakeStringify(appTaskRequest));
                    JSONObject addAppTaskObj = JSONObject.parseObject(addAppTaskRsp);
                    log.info("add app task response:{}", addAppTaskObj.toJSONString());
                    if (ResponseUtils.checkResponse(addAppTaskObj)) {
                        // 第三方任务id
                        thirdTaskId = addAppTaskObj.getJSONObject(BaseConstants.STR_DATA).getString("uuid");

                    } else {
                        log.error("add app task failed");
                        return ServiceResult.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
                    }
                } catch (Exception e) {
                    log.error("add app task error", e);
                    return ServiceResult.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
                }
            }
        } else {
            urlPath = "/tasks/standards/action";

            // 判断是否关联流量平台任务，没有则新增
            if (StringUtils.isBlank(thirdTaskId)) {
                // 封装请求参数
                AddStandardsTaskRequest standTaskRequest = new AddStandardsTaskRequest(flowTaskModel);
                standTaskRequest.setPort(flowPortModel.getServerMac());

                try {
                    String addStandTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/standards",
                            JsonUtils.snakeStringify(standTaskRequest));
                    JSONObject addStandTaskObj = JSONObject.parseObject(addStandTaskRsp);
                    log.info("add standard task response:{}", addStandTaskObj.toJSONString());
                    if (ResponseUtils.checkResponse(addStandTaskObj)) {
                        // 第三方任务id
                        thirdTaskId = addStandTaskObj.getString(BaseConstants.STR_DATA);
                    } else {
                        log.error("add standard task failed");
                        return ServiceResult.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
                    }
                } catch (Exception e) {
                    log.error("add standard task error", e);
                    return ServiceResult.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
                }
            }
        }

        int taskStatus = TaskStatusEnum.RUNNING.toValue();
        // 构建执行任务请求
        FlowExecuteRequest executeRequest = new FlowExecuteRequest(flowTaskModel);
        executeRequest.setTaskId(flowTaskModel.getId());
        executeRequest.setAction(taskAction);
        executeRequest.setUuid(thirdTaskId);
        executeRequest.setUrlPath(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + urlPath);
        if (TaskActionEnum.STOP.toValue() == taskAction) {
            // 停止任务时，执行次数设为1
            executeRequest.setCount(1);
            taskStatus = TaskStatusEnum.STOPING.toValue();
        }

        // 调用定时任务处理
        FlowExecuteScheduleUtil util = FlowExecuteScheduleUtil.builder().build();
        util.setFlowBaseModel(executeRequest);
        util.setMethod(flowExecuteScheduleMethod);
        util.schedule();

        // 更新第三方任务id和任务状态
        FlowTaskModel updateTaskModel = new FlowTaskModel();
        updateTaskModel.setId(flowTaskModel.getId());
        updateTaskModel.setThirdTaskId(thirdTaskId);
        updateTaskModel.setTaskStatus(taskStatus);
        updateTaskModel.setUpdateTime(LocalDateTime.now());
        this.updateFlowTaskInfo(updateTaskModel);

        TaskCommon.executeTask(() -> {
            try {
                asyncPlatFlowTaskService.refreshTaskStatus(flowTaskModel.getId(), flowActuatorModel, callback, errorCallback);
            } catch (Exception e) {
                log.info("refresh task status error: ", e);
            }
        });


        return ServiceResult.success(true);
    }

    /**
     * 查询流量执行记录
     * @param taskId
     * @return
     */
    public ServiceResult<FlowRecordsResponse> queryFlowRecords(String taskId) {
        // 批量任务传的是nodeId，直接用该值查询
        String thirdTaskId = taskId;
        FlowRecordsResponse response = new FlowRecordsResponse();
        FlowTaskModel flowTaskModel = getModelMapper().get(taskId);
        if (Objects.isNull(flowTaskModel)) {
            HumanCondition condition = new HumanCondition().filterEqual(FlowTaskModel::getNodeId, taskId);
            List<FlowTaskModel> flowTaskModelList = getModelMapper().list(condition);
            if (CollectionUtils.isEmpty(flowTaskModelList)) {
                log.error("task info is empty for taskId:{}", taskId);
                // 流量任务不存在
                return ServiceResult.fail(FlowErrorCode.TASK_NOT_EXIST);
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

            if (Objects.nonNull(flowRecordsArray)) {
                flowRecordsList = flowRecordsArray.toJavaList(FlowRecords.class);
            }
            response.setFlowRecordsList(flowRecordsList);
            response.setStatus(status);
            response.setResult(result);
        }

        return ServiceResult.success(response);
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
            log.error("query task status error", e);
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
    public int batchUpdateTaskStatus(Integer taskStatus, Boolean runCount, List<String> taskIds) {
        return getModelMapper().batchUpdateTaskStatus(taskStatus, runCount, taskIds);
    }

    /**
     * 更新流量任务
     * @param flowTaskModel
     * @return
     */
    public int updateFlowTaskInfo(FlowTaskModel flowTaskModel) {
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

    /**
     * 批量执行流量任务
     * @param request
     * @return
     */
    public ServiceResult<Boolean> batchActuator(BatchActuatorTaskRequest request) {
        log.info("=========== Batch actuator flow tasks start ===============");
        // 流量任务存在校验
        QueryParams queryParams = new QueryParams();
        queryParams.setIds(request.getTaskIds().toArray(new String[0]));
        ListObjServiceResult<FlowTaskModel> objServiceResult = list(queryParams);
        if (Objects.isNull(objServiceResult) || !Objects.equals(request.getTaskIds().size(), objServiceResult.getSource().getTotal())) {
            log.error("not all flow task exist");
            return ServiceResult.fail(FlowErrorCode.TASK_NOT_EXIST);
        }

        List<FlowTaskModel> flowTaskModels = objServiceResult.getSource().getObjs();
        if (TaskActionEnum.STOP.toValue() != request.getTaskAction()) {
            // 流量任务状态校验
            List<String> runningTaskIds = flowTaskModels.stream().filter(flowTaskModel ->
                    flowTaskModel.getTaskStatus() == TaskStatusEnum.RUNNING.toValue()).map(FlowTaskModel::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(runningTaskIds)) {
                log.error("tasks [{}] are running, can not batch actuator tasks", runningTaskIds);
                return ServiceResult.fail(FlowErrorCode.TASK_IS_RUNNING);
            }
        }

        // 流量执行器信息查询
        String actuatorId = flowTaskModels.get(0).getActuatorId();
        FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            return ServiceResult.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }

        // 第三方任务id集合
        List<String> thirdTaskIds = flowTaskModels.stream().map(FlowTaskModel::getThirdTaskId).distinct().collect(Collectors.toList());
        // 任务id集合
        List<String> taskIds = flowTaskModels.stream().map(FlowTaskModel::getId).distinct().collect(Collectors.toList());

        // 执行任务参数
        JSONObject object = new JSONObject();
        object.put("uuids", thirdTaskIds);
        object.put("action", request.getTaskAction());
        object.put("run_type", request.getRunType());
        object.put("node_id", flowTaskModels.get(0).getNodeId());
        object.put("time", TimeCommon.localDateTimeToString(LocalDateTime.now()));

        try {
            // 执行任务地址
            String urlPath = CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/batch/action";
            String batchActuatorRsp = HttpUtils.sendPost(urlPath, JSONObject.toJSONString(object));
            JSONObject batchActuatorObj = JSONObject.parseObject(batchActuatorRsp);
            log.info("batch actuator tasks response:{}", batchActuatorObj.toJSONString());
            if (ResponseUtils.checkResponse(batchActuatorObj)) {
                log.info("batch actuator tasks success");
                // 流量任务状态和执行次数更新
                this.batchUpdateTaskStatus(TaskStatusEnum.RUNNING.toValue(), true, taskIds);
            } else {
                log.error("batch actuator tasks failed");
                return ServiceResult.success(false);
            }
        } catch (IOException e) {
            log.error("batch actuator tasks error", e);
            return ServiceResult.success(false);
        }

        TaskCommon.executeTask(() -> {
            try {
                // 批量轮询任务状态
                asyncPlatFlowTaskService.batchRefreshTaskStatus(flowTaskModels, flowActuatorModel, request.getCallbackConsumer());
            } catch (Exception e) {
                log.info("refresh task status error: ", e);
            }
        });

        log.info("=========== Batch actuator flow tasks end ===============");
        return ServiceResult.success(true);
    }

}
