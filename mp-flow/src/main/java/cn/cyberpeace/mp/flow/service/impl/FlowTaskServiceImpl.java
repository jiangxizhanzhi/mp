package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.common.TimeCommon;
import cn.cyberpeace.mp.base.constant.BaseSingleton;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
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
import cn.cyberpeace.mp.flow.bo.FlowStatisticsDetail;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsInfo;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.*;
import cn.cyberpeace.mp.flow.model.*;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.*;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.response.FlowRecordsResponse;
import cn.cyberpeace.mp.flow.service.FlowPortService;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import cn.cyberpeace.mp.flow.service.FlowTaskService;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


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

    
    public ServiceResult<FlowTaskModel> getDetail(String id, String[] fields) {
        FlowTaskModel flowTaskModel = new FlowTaskModel();
        ObjResult<FlowTaskModel> serviceResult = get(id, fields).getSource();
        if (Objects.nonNull(serviceResult)) {
            flowTaskModel = serviceResult.getObj();

            if (StringUtils.isNotBlank(flowTaskModel.getThirdTaskId()) &&
                    FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform() &&
                    (TaskStatusEnum.RUNNING.toValue() == flowTaskModel.getTaskStatus()) ||
                    TaskStatusEnum.STOPING.toValue() == flowTaskModel.getTaskStatus()) {
                
                
                String actuatorId = flowTaskModel.getActuatorId();
                FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
                if (Objects.isNull(flowActuatorModel)) {
                    return ResultCommon.success(flowTaskModel);
                }

                JSONArray rows = this.queryTaskStatus(Collections.singletonList(flowTaskModel.getThirdTaskId()), CommonUtil.getHostUrl(flowActuatorModel.getIp()));
                if (Objects.nonNull(rows)) {
                    int taskStatus = rows.getJSONObject(0).getInteger("status");
                    flowTaskModel.setTaskStatus(taskStatus);

                    
                    update(flowTaskModel);
                }
            }
        }

        return ResultCommon.success(flowTaskModel);
    }


    
    public ListObjResult<FlowTaskModel> pageFlowTask(QueryParams queryParams) {
        ListObjResult<FlowTaskModel> listObjResult = list(queryParams).getSource();
        if (CollectionUtils.isNotEmpty(listObjResult.getList())) {
            
            List<FlowTaskModel> taskModelList = listObjResult.getList().stream().filter(model ->
                    FlowPlatformEnum.TREX.toValue() == model.getFlowPlatform() &&
                            StringUtils.isNotBlank(model.getThirdTaskId()) &&
                            (TaskStatusEnum.RUNNING.toValue() == model.getTaskStatus() ||
                                    (TaskStatusEnum.STOPING.toValue() == model.getTaskStatus())))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(taskModelList)) {
                
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
                                
                                task.setTaskStatus(obj.getInteger("status"));
                            }
                        }
                    });

                    
                    bulkUpdate(taskModelList);
                } else {
                    log.error("query task status failed");
                }
            }
        }

        return listObjResult.asResult();
    }

    
    public ServiceResult<FlowTaskModel> addFlowTask(AddFlowTaskRequest request) {
        if (StringUtils.isBlank(request.getOriginIps())) {
            
            request.setOriginIps("127.0.0.1-127.0.0.1");
        }

        FlowTaskModel flowTaskModel = request;
        
        Boolean isCustom = StringUtils.isNotBlank(flowTaskModel.getFlowProtocol());

        if (isCustom) {
            
            create(flowTaskModel);
            
            asyncPlatFlowTaskService.savePlatFlowTask(request, false);
            return ResultCommon.success(flowTaskModel);
        } else {
            ObjResult<FlowInfoModel> flowInfoObjResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
            if (Objects.nonNull(flowInfoObjResult)) {
                FlowInfoModel flowInfoModel = flowInfoObjResult.asResult();

                if (FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform()) {
                    if (asyncPlatFlowTaskService.uploadFlowInfo(flowInfoModel, flowTaskModel, request)) {
                        create(flowTaskModel);

                        
                        asyncPlatFlowTaskService.savePlatFlowTask(request, false);
                        return ResultCommon.success(flowTaskModel);
                    } else {
                        return new ServiceResult<>(null, FlowErrorCode.ADD_TASK_ERROR);
                    }
                } else {
                    
                    create(flowTaskModel);
                    return ResultCommon.success(flowTaskModel);
                }
            } else {
                log.error("flow file be chosen not existed");
                return new ServiceResult<>(null, FlowErrorCode.ADD_TASK_ERROR);
            }
        }
    }


    
    public ServiceResult<FlowTaskModel> updateFlowTask(AddFlowTaskRequest request) {
        if (StringUtils.isBlank(request.getOriginIps())) {
            
            request.setOriginIps("127.0.0.1-127.0.0.1");
        }
        FlowTaskModel flowTaskModel = request;
        ObjResult<FlowTaskModel> flowTaskResult = this.get(flowTaskModel.getId()).getSource();
        if (Objects.isNull(flowTaskResult)) {
            log.error("flow task not exist");
            return ResultCommon.fail(FlowErrorCode.TASK_NOT_EXIST);
        }
        FlowTaskModel oldTaskModel = flowTaskResult.asResult();
        
        String actuatorId = flowTaskModel.getActuatorId();
        FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
        String resourceUrl = CommonUtil.getResourcetUrl(flowActuatorModel.getIp());

        ObjResult<FlowInfoModel> oldFlowInfoResult = flowInfoService.get(oldTaskModel.getFlowId()).getSource();

        if (FlowPlatformEnum.TREX.toValue() == oldTaskModel.getFlowPlatform()) {
            if (Objects.nonNull(oldFlowInfoResult)) {
                FlowInfoModel oldFlowInfoModel = oldFlowInfoResult.asResult();
                
                if (!oldTaskModel.getFlowId().equals(flowTaskModel.getFlowId())) {
                    
                    if (!this.deleteFlowFile(oldFlowInfoModel.getResourceType(),
                            Collections.singletonList(oldTaskModel.getThirdFlowId()), resourceUrl)) {
                        log.warn("delete old flow file error");
                    }

                    
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

            
            asyncPlatFlowTaskService.savePlatFlowTask(request, true);
        }

        
        update(flowTaskModel);
        return ResultCommon.success(flowTaskModel);
    }

    
    public ServiceResult<Boolean> deleteFlowTask(String id) {
        ObjResult<FlowTaskModel> flowTaskResult = get(id).getSource();
        if (Objects.nonNull(flowTaskResult)) {
            FlowTaskModel flowTaskModel = flowTaskResult.asResult();

            ObjResult<FlowInfoModel> flowInfoResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
            if (Objects.nonNull(flowInfoResult)) {
                if (FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform()) {
                    
                    String actuatorId = flowTaskModel.getActuatorId();
                    FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
                    String resourceUrl = CommonUtil.getResourcetUrl(flowActuatorModel.getIp());

                    FlowInfoModel oldModel = flowInfoResult.asResult();
                    
                    if (this.deleteFlowFile(oldModel.getResourceType(),
                            Collections.singletonList(flowTaskModel.getThirdFlowId()), resourceUrl)) {
                        log.info("delete flow file success");
                    } else {
                        log.error("delete flow file error");
                    }

                    
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
                
                List<String> pcapIdList =
                        infoModelList.stream().filter(model -> (ResourceTypeEnum.PACP.toValue() == model.getResourceType())).map(FlowInfoModel::getId).collect(Collectors.toList());
                pcapTaskModels = flowTaskList.stream().filter(task ->
                                FlowPlatformEnum.TREX.toValue() == task.getFlowPlatform() &&
                                        pcapIdList.contains(task.getFlowId())).collect(Collectors.toList());

                
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
            return ResultCommon.fail(FlowErrorCode.NO_TASK_TO_DELETE);
        }

        ServiceResult<Integer> buldDelResult = bulkDelete(taskDelIds);

        
        if (CollectionUtils.isNotEmpty(pcapTaskModels)) {
            asyncPlatFlowTaskService.deletePlatFlowTask(ResourceTypeEnum.PACP.toValue(), pcapTaskModels);
        }
        if (CollectionUtils.isNotEmpty(scriptTaskModels)) {
            asyncPlatFlowTaskService.deletePlatFlowTask(ResourceTypeEnum.SCRIPT.toValue(), scriptTaskModels);
        }

        return buldDelResult;
    }

    
    private boolean deleteFlowFile(int resourceType, List<String> thirdFlowIds, String resourceUrl) {
        String delUrl = "";
        if (ResourceTypeEnum.PACP.toValue() == resourceType) {
            log.info("start to delete pcap");
            
            delUrl = "/pcap";
        } else {
            log.info("start to delete scripts");
            
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


    
    public ServiceResult<Boolean> actuator(String flowTaskId, int taskAction, Runnable callback, Consumer<String> errorCallback) {
        
        ObjResult<FlowTaskModel> flowTaskResult = get(flowTaskId).getSource();
        if (Objects.isNull(flowTaskResult)) {
            log.error("flow task not exist:{}", flowTaskId);
            return ResultCommon.fail(FlowErrorCode.TASK_NOT_EXIST);
        }
        FlowTaskModel flowTaskModel = flowTaskResult.getObj();

        
        if (FlowPlatformEnum.TREX.toValue() == flowTaskModel.getFlowPlatform()) {
            log.info("=========== Actuator trex flow end ===============");
            return this.actuatorTrex(flowTaskModel, taskAction, callback, errorCallback);
        } else {
            log.info("=========== Actuator other flow end ===============");
            return ResultCommon.fail(FlowErrorCode.NO_ACTUATOR_TO_USE);
        }
    }

    
    private ServiceResult<Boolean> actuatorTrex(FlowTaskModel flowTaskModel, int taskAction, Runnable callback, Consumer<String> errorCallback) {
        FlowInfoModel flowInfoModel = new FlowInfoModel();
        
        boolean isCustom = false;
        if (Objects.equals(CustomFlagEnum.NO.toValue(), flowTaskModel.getCustomFlag())) {
            
            ObjResult<FlowInfoModel> flowInfoResult = flowInfoService.get(flowTaskModel.getFlowId()).getSource();
            if (Objects.isNull(flowInfoResult)) {
                log.error("flow info in task not exist:{}", flowTaskModel.getFlowId());
                return ResultCommon.fail(FlowErrorCode.FLOW_IN_TASK_NOT_EXIST);
            }
            flowInfoModel = flowInfoResult.getObj();
        } else {
            isCustom = true;
            flowInfoModel.setResourceType(ResourceTypeEnum.PACP.toValue());
        }

        
        String actuatorId = flowTaskModel.getActuatorId();
        FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }

        ObjResult<FlowPortModel> portObjResult = flowPortService.get(flowTaskModel.getFlowPortId()).getSource();
        if (Objects.isNull(portObjResult)) {
            if (Objects.equals(CustomFlagEnum.YES.toValue(), flowTaskModel.getCustomFlag())) {
                log.error("custom flow can only run once");
                return ResultCommon.fail(FlowErrorCode.RECREATE_CUSTOM_FLOW_ERROR);
            } else {
                log.error("flow port info is empty");
                return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
            }
        }
        FlowPortModel flowPortModel = portObjResult.asResult();

        String thirdTaskId = flowTaskModel.getThirdTaskId();
        if (StringUtils.isNotBlank(thirdTaskId)) {
            
            if (TaskActionEnum.START.toValue() == taskAction) {
                JSONArray rows = this.queryTaskStatus(Collections.singletonList(thirdTaskId), CommonUtil.getHostUrl(flowActuatorModel.getIp()));
                if (Objects.nonNull(rows)) {
                    int taskStatus = rows.getJSONObject(0).getInteger("status");
                    if (TaskStatusEnum.RUNNING.toValue() == taskStatus || TaskStatusEnum.STOPING.toValue() == taskStatus) {
                        return ResultCommon.fail(FlowErrorCode.TASK_IS_RUNNING);
                    }
                } else {
                    log.error("query task status failed");
                    return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
                }
            }
        } else {
            
            if (TaskActionEnum.STOP.toValue() == taskAction) {
                log.error("third task not exist");
                return ResultCommon.fail(FlowErrorCode.NO_RUNNING_TASK_TO_STOP);
            }
        }

        
        String urlPath = "";
        if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
            if (isCustom) {
                
                urlPath = "/tasks/custom/action";
            } else {
                
                urlPath = "/tasks/action";
            }

            
            if (StringUtils.isBlank(thirdTaskId)) {
                
                AddAppTaskRequest appTaskRequest = new AddAppTaskRequest(flowTaskModel, flowPortModel);
                try {
                    String addAppTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/app",
                            JsonUtils.snakeStringify(appTaskRequest));
                    JSONObject addAppTaskObj = JSONObject.parseObject(addAppTaskRsp);
                    log.info("add app task response:{}", addAppTaskObj.toJSONString());
                    if (ResponseUtils.checkResponse(addAppTaskObj)) {
                        
                        thirdTaskId = addAppTaskObj.getJSONObject(BaseConstants.STR_DATA).getString("uuid");

                    } else {
                        log.error("add app task failed");
                        return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
                    }
                } catch (Exception e) {
                    log.error("add app task error, {}", ExceptionUtils.getStackTrace(e));
                    return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
                }
            }
        } else {
            urlPath = "/tasks/standards/action";

            
            if (StringUtils.isBlank(thirdTaskId)) {
                
                AddStandardsTaskRequest standTaskRequest = new AddStandardsTaskRequest(flowTaskModel);
                standTaskRequest.setPort(flowPortModel.getServerMac());

                try {
                    String addStandTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/standards",
                            JsonUtils.snakeStringify(standTaskRequest));
                    JSONObject addStandTaskObj = JSONObject.parseObject(addStandTaskRsp);
                    log.info("add standard task response:{}", addStandTaskObj.toJSONString());
                    if (ResponseUtils.checkResponse(addStandTaskObj)) {
                        
                        thirdTaskId = addStandTaskObj.getString(BaseConstants.STR_DATA);
                    } else {
                        log.error("add standard task failed");
                        return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
                    }
                } catch (Exception e) {
                    log.error("add standard task error, {}", ExceptionUtils.getStackTrace(e));
                    return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
                }
            }
        }

        int taskStatus = TaskStatusEnum.RUNNING.toValue();
        
        FlowExecuteRequest executeRequest = new FlowExecuteRequest(flowTaskModel);
        executeRequest.setTaskId(flowTaskModel.getId());
        executeRequest.setAction(taskAction);
        executeRequest.setUuid(thirdTaskId);
        executeRequest.setUrlPath(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + urlPath);
        if (TaskActionEnum.STOP.toValue() == taskAction) {
            
            executeRequest.setCount(1);
            taskStatus = TaskStatusEnum.STOPING.toValue();
        }

        
        FlowExecuteScheduleUtil util = FlowExecuteScheduleUtil.builder().build();
        util.setFlowBaseModel(executeRequest);
        util.setMethod(flowExecuteScheduleMethod);
        util.schedule();

        
        FlowTaskModel updateTaskModel = new FlowTaskModel();
        updateTaskModel.setId(flowTaskModel.getId());
        updateTaskModel.setThirdTaskId(thirdTaskId);
        updateTaskModel.setTaskStatus(taskStatus);
        updateTaskModel.setUpdateTime(LocalDateTime.now());
        this.updateFlowTaskDetail(updateTaskModel);

        TaskCommon.executeTask(() -> {
            try {
                asyncPlatFlowTaskService.refreshTaskStatus(flowTaskModel.getId(), flowActuatorModel, callback, errorCallback);
            } catch (Exception e) {
                log.info("refresh task status error: ", e);
            }
        });


        return ResultCommon.success(true);
    }

    
    public ServiceResult<FlowRecordsResponse> queryFlowRecords(String taskId) {
        
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
                
                return ResultCommon.success(new FlowRecordsResponse(null, null, new ArrayList<>()));
            } else {
                flowTaskModel = flowTaskModelList.get(0);
            }
        } else {
            
            thirdTaskId = flowTaskModel.getThirdTaskId();
        }

        
        String actuatorId = flowTaskModel.getActuatorId();
        FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            log.error("flowActuatorModel is empty for actuatorId:{}", actuatorId);
            
            throw new MpRuntimeException(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }

        JSONArray rows = this.queryTaskStatus(Collections.singletonList(thirdTaskId), CommonUtil.getHostUrl(flowActuatorModel.getIp()));
        if (CollectionUtils.isNotEmpty(rows)) {
            List<FlowRecords> flowRecordsList = new ArrayList<>();
            JSONArray flowRecordsArray = rows.getJSONObject(0).getJSONArray("flow_records");
            
            Integer status = rows.getJSONObject(0).getInteger("status");
            
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

        return ResultCommon.success(response);
    }

    
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

    
    public int increaseRunningCount(String taskId) {
        return getModelMapper().increaseRunningCount(taskId);
    }

    
    public int batchUpdateTaskStatus(Integer taskStatus, Boolean runCount, List<String> taskIds) {
        return getModelMapper().batchUpdateTaskStatus(taskStatus, runCount, taskIds);
    }

    
    public int updateFlowTaskInfo(FlowTaskModel flowTaskModel) {
        if (StringUtils.isNotBlank(flowTaskModel.getOriginIps())) {
            
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
        AddFlowTaskRequest addFlowTaskRequest = new AddFlowTaskRequest();
        BeanUtils.copyProperties(flowTaskModel, addFlowTaskRequest);
        asyncPlatFlowTaskService.savePlatFlowTask(addFlowTaskRequest, true);
        return result;
    }

    
    public int updateFlowTaskDetail(FlowTaskModel flowTaskModel) {
        return getModelMapper().updateTaskInfo(flowTaskModel);
    }


    
    public List<FlowTaskModel> batchSavePlatFlowTask(List<AddFlowTaskRequest> addTaskRequestList) {
        return asyncPlatFlowTaskService.batchSavePlatFlowTask(addTaskRequestList);
    }

    
    public ServiceResult<Boolean> batchActuator(BatchActuatorTaskRequest request) {
        
        QueryParams queryParams = new QueryParams();
        queryParams.setIds(request.getTaskIds().toArray(new String[0]));
        ListObjServiceResult<FlowTaskModel> objServiceResult = list(queryParams);
        if (Objects.isNull(objServiceResult) || !Objects.equals(request.getTaskIds().size(), objServiceResult.getSource().getTotal())) {
            log.error("not all flow task exist");
            return ResultCommon.fail(FlowErrorCode.TASK_NOT_EXIST);
        }

        List<FlowTaskModel> flowTaskModels = objServiceResult.getSource().getObjs();
        if (TaskActionEnum.STOP.toValue() != request.getTaskAction()) {
            
            List<String> runningTaskIds = flowTaskModels.stream().filter(flowTaskModel ->
                    flowTaskModel.getTaskStatus() == TaskStatusEnum.RUNNING.toValue()).map(FlowTaskModel::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(runningTaskIds)) {
                log.error("tasks [{}] are running, can not batch actuator tasks", runningTaskIds);
                return ResultCommon.fail(FlowErrorCode.TASK_IS_RUNNING);
            }
        }

        
        String actuatorId = flowTaskModels.get(0).getActuatorId();
        FlowActuatorModel flowActuatorModel = asyncPlatFlowTaskService.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }

        
        List<String> thirdTaskIds = new ArrayList<>();
        request.getTaskIds().forEach(taskId -> {
            Optional<FlowTaskModel> optionalFlowTaskModel =
                    flowTaskModels.stream().filter(taskModel -> taskId.equals(taskModel.getId())).findFirst();
            if (optionalFlowTaskModel.isPresent()) {
                FlowTaskModel sortTaskModel = optionalFlowTaskModel.get();
                thirdTaskIds.add(sortTaskModel.getThirdTaskId());
            }
        });
        log.info("Batch actuator flow thirdTaskIds: {}", thirdTaskIds);
        
        List<String> taskIds = flowTaskModels.stream().map(FlowTaskModel::getId).distinct().collect(Collectors.toList());

        
        JSONObject object = new JSONObject();
        object.put("uuids", thirdTaskIds);
        object.put("action", request.getTaskAction());
        object.put("run_type", request.getRunType());
        object.put("node_id", flowTaskModels.get(0).getNodeId());
        object.put("time", TimeCommon.localDateTimeToString(LocalDateTime.now()));

        try {
            
            String urlPath = CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/batch/action";
            String batchActuatorRsp = HttpUtils.sendPost(urlPath, JSONObject.toJSONString(object));
            JSONObject batchActuatorObj = JSONObject.parseObject(batchActuatorRsp);
            log.info("batch actuator tasks response:{}", batchActuatorObj.toJSONString());
            if (ResponseUtils.checkResponse(batchActuatorObj)) {
                log.info("batch actuator tasks success");
                
                this.batchUpdateTaskStatus(TaskStatusEnum.RUNNING.toValue(), true, taskIds);
            } else {
                log.error("batch actuator tasks failed");
                return ResultCommon.success(false);
            }
        } catch (IOException e) {
            log.error("batch actuator tasks error, {}", ExceptionUtils.getStackTrace(e));
            return ResultCommon.success(false);
        }

        TaskCommon.executeTask(() -> {
            try {
                
                asyncPlatFlowTaskService.batchRefreshTaskStatus(flowTaskModels, flowActuatorModel, request.getCallbackConsumer());
            } catch (Exception e) {
                log.info("refresh task status error: ", e);
            }
        });
        return ResultCommon.success(true);
    }

}
