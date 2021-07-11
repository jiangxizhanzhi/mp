package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.CheckUtils;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.NetworkUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.flow.bo.*;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.*;
import cn.cyberpeace.mp.flow.model.*;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowPortMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.*;
import cn.cyberpeace.mp.flow.response.BatchActuatorCallbackResponse;
import cn.cyberpeace.mp.flow.service.*;
import cn.cyberpeace.mp.flow.util.*;
import cn.cyberpeace.mp.situationaware.utils.MessageCommonUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AsyncPlatFlowTaskService {

    @Autowired
    private FlowConfig flowConfig;

    @Autowired
    private FlowTaskMapper flowTaskMapper;

    @Autowired
    private FlowInfoMapper flowInfoMapper;

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    @Autowired
    private FlowPortMapper flowPortMapper;

    @Autowired
    private FlowPortService flowPortService;

    @Autowired
    private FlowScriptParamService flowScriptParamService;

    @Autowired
    private FlowTaskPortService flowTaskPortService;

    @Autowired
    private FlowTaskTaskService flowTaskTaskService;

    @Autowired
    private FlowTaskConfService flowTaskConfService;

    @Autowired
    private FlowTaskRecordService flowTaskRecordService;

    
    private static final String TGN_PATH = "/root/.mp/resources/flows/traffic_generators.zip";

    

    public void savePlatFlowTask(AddFlowTaskRequest request, boolean isUpdate) {
        log.info("start to " + (isUpdate ? "update" : "add") + "platform flow task");

        FlowTaskModel flowTaskModel = request;
        
        if (Objects.isNull(flowTaskModel)) {
            log.error("flow task not exist");
            return;
        }

        
        if (isUpdate) {
            if (StringUtils.isBlank(flowTaskModel.getThirdTaskId())) {
                log.error("third flow task is not exist");
                return;
            }
        } else {
            if (StringUtils.isNotBlank(flowTaskModel.getThirdTaskId())) {
                log.error("third flow task is already exist");
                return;
            }
        }

        
        Boolean isCustom = StringUtils.isNotBlank(flowTaskModel.getFlowProtocol());
        FlowInfoModel flowInfoModel = new FlowInfoModel();
        if (!isCustom) {
            flowTaskModel.setCustomFlag(CustomFlagEnum.NO.toValue());
            
            flowInfoModel = flowInfoMapper.get(flowTaskModel.getFlowId());
            if (Objects.isNull(flowInfoModel)) {
                log.error("flow info in task not exist:{}", flowTaskModel.getFlowId());
                return;
            }
        } else {
            log.info("create custom flow task");
            flowTaskModel.setCustomFlag(CustomFlagEnum.YES.toValue());
            flowInfoModel.setResourceType(ResourceTypeEnum.PACP.toValue());
        }

        
        String actuatorId = flowTaskModel.getActuatorId();
        FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            return;
        }

        
        FlowPortModel flowPortModel = null;
        if (isUpdate) {
            
            flowPortModel = flowPortMapper.get(flowTaskModel.getFlowPortId());
            if (Objects.isNull(flowPortModel)) {
                log.error("update flow port info error");
                return;
            }
        } else {
            List<FlowPortModel> attackFlowPorts =
                    flowPortMapper.list(new HumanCondition()
                            .filterEqual(FlowPortModel::getNetworkId, request.getNetworkId())
                            .filterEqual(FlowPortModel::getActuatorId, actuatorId));
            if (CollectionUtils.isNotEmpty(attackFlowPorts)) {
                flowPortModel = attackFlowPorts.get(0);
            } else {
                log.error("get flow port info error");
                return;
            }
        }

        flowTaskModel.setFlowPortId(flowPortModel.getId());

        String thirdTaskId = "";
        if (isCustom || ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
            
            AddAppTaskRequest appTaskRequest = new AddAppTaskRequest(flowTaskModel, flowPortModel);

            try {
                String saveAppTaskRsp = null;
                if (isUpdate) {
                    
                    saveAppTaskRsp = HttpUtils.sendPatch(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/app/"
                                    + flowTaskModel.getThirdTaskId(), JsonUtils.snakeStringify(appTaskRequest));
                } else {
                    
                    saveAppTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/app",
                            JsonUtils.snakeStringify(appTaskRequest));
                }

                JSONObject saveAppTaskObj = JSONObject.parseObject(saveAppTaskRsp);
                log.info("save app task response:{}", saveAppTaskObj.toJSONString());
                if (ResponseUtils.checkResponse(saveAppTaskObj)) {
                    
                    thirdTaskId = saveAppTaskObj.getJSONObject(BaseConstants.STR_DATA).getString("uuid");
                } else {
                    log.error("save app task failed");
                    throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
                }
            } catch (IOException e) {
                log.error("save app task error", e);
            }
        } else {
            try {
                
                AddStandardsTaskRequest standTaskRequest = new AddStandardsTaskRequest(flowTaskModel);
                standTaskRequest.setPort(flowPortModel.getServerMac());
                String saveStandTaskRsp = null;
                if (isUpdate) {
                    saveStandTaskRsp = HttpUtils.sendPatch(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/standards/" +
                                    flowTaskModel.getThirdTaskId(), JsonUtils.snakeStringify(standTaskRequest));
                } else {
                    saveStandTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/standards",
                            JsonUtils.snakeStringify(standTaskRequest));
                }

                JSONObject saveStandTaskObj = JSONObject.parseObject(saveStandTaskRsp);
                log.info("save standard task response:{}", saveStandTaskObj.toJSONString());
                if (ResponseUtils.checkResponse(saveStandTaskObj)) {
                    
                    thirdTaskId = saveStandTaskObj.getString(BaseConstants.STR_DATA);
                } else {
                    log.error("save standard task failed");
                    throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
                }
            } catch (IOException e) {
                log.error("save standard task error", e);
            }
        }

        
        if (StringUtils.isNotBlank(thirdTaskId)) {
            flowTaskModel.setThirdTaskId(thirdTaskId);
            flowTaskMapper.update(flowTaskModel);
        }

    }

    
    public List<FlowTaskModel> batchSavePlatFlowTask(List<AddFlowTaskRequest> addTaskRequestList) {
        log.info("start to batch save platform flow task");
        List<FlowTaskModel> flowTaskModelList = new ArrayList<>();
        if (CollectionUtils.isEmpty(addTaskRequestList)) {
            log.error("batch save task request list is empty");
            return flowTaskModelList;
        }

        
        for (AddFlowTaskRequest request : addTaskRequestList) {
            FlowTaskModel flowTaskModel = request;
            flowTaskModelList.add(flowTaskModel);
            
            if (Objects.isNull(flowTaskModel)) {
                log.error("flow task not exist");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }

            
            if (StringUtils.isNotBlank(flowTaskModel.getId())) {
                log.info("third flow task is already exist");
                continue;
            }

            FlowInfoModel flowInfoModel = new FlowInfoModel();
            
            flowInfoModel = flowInfoMapper.get(flowTaskModel.getFlowId());
            if (Objects.isNull(flowInfoModel)) {
                log.error("flow info in task not exist:{}", flowTaskModel.getFlowId());
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }

            
            if (!uploadFlowInfo(flowInfoModel, flowTaskModel, request)) {
                log.error("upload flow file info error");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }

            
            String actuatorId = flowTaskModel.getActuatorId();
            FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
            if (Objects.isNull(flowActuatorModel)) {
                log.error("flowActuatorModel is empty");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }

            String thirdTaskId = "";
            
            FlowPortModel flowPortModel = null;
            List<FlowPortModel> flowPorts =
                    flowPortMapper.list(new HumanCondition()
                            .filterEqual(FlowPortModel::getNetworkId, request.getNetworkId())
                            .filterEqual(FlowPortModel::getActuatorId, actuatorId));
            if (CollectionUtils.isNotEmpty(flowPorts)) {
                flowPortModel = flowPorts.get(0);
            } else {
                log.error("batch get flow port info error");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }
            
            flowTaskModel.setFlowPortId(flowPortModel.getId());

            if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
                
                try {
                    AddAppTaskRequest appTaskRequest = new AddAppTaskRequest(flowTaskModel, flowPortModel);
                    String saveAppTaskRsp = null;
                    
                    saveAppTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/app",
                            JsonUtils.snakeStringify(appTaskRequest));

                    JSONObject saveAppTaskObj = JSONObject.parseObject(saveAppTaskRsp);
                    log.info("save background task response:{}", saveAppTaskObj.toJSONString());
                    if (ResponseUtils.checkResponse(saveAppTaskObj)) {
                        
                        thirdTaskId = saveAppTaskObj.getJSONObject(BaseConstants.STR_DATA).getString("uuid");
                    } else {
                        log.error("save background task failed");
                        throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
                    }
                } catch (IOException e) {
                    log.error("save background task error", e);
                }
            } else {
                try {
                    
                    AddStandardsTaskRequest standTaskRequest = new AddStandardsTaskRequest(flowTaskModel);
                    standTaskRequest.setPort(flowPortModel.getServerMac());
                    String saveStandTaskRsp = null;
                    saveStandTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/standards",
                            JsonUtils.snakeStringify(standTaskRequest));

                    JSONObject saveStandTaskObj = JSONObject.parseObject(saveStandTaskRsp);
                    log.info("save attack task response:{}", saveStandTaskObj.toJSONString());
                    if (ResponseUtils.checkResponse(saveStandTaskObj)) {
                        
                        thirdTaskId = saveStandTaskObj.getString(BaseConstants.STR_DATA);
                    } else {
                        log.error("save attack task failed");
                        throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
                    }
                } catch (IOException e) {
                    log.error("save attack task error", e);
                }
            }

            
            if (StringUtils.isNotBlank(thirdTaskId)) {
                flowTaskModel.setThirdTaskId(thirdTaskId);
            }
            flowTaskMapper.create(flowTaskModel);
        }

        return flowTaskModelList;
    }

    
    @Async
    public void deletePlatFlowTask(int resourceType, List<FlowTaskModel> flowTaskModels) {
        if (CollectionUtils.isEmpty(flowTaskModels)) {
            log.warn("no platform flow task to delete");
            return;
        }

        
        String actuatorId = flowTaskModels.get(0).getActuatorId();
        FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            return;
        }

        List<String> flowPortIds = flowTaskModels.stream().map(FlowTaskModel::getFlowPortId).collect(Collectors.toList());
        List<String> thirdTaskIds = flowTaskModels.stream().map(FlowTaskModel::getThirdTaskId).collect(Collectors.toList());
        String delUrl = "";
        if (ResourceTypeEnum.PACP.toValue() == resourceType) {
            log.info("start to delete app task");
            
            delUrl = "/tasks/app";
        } else {
            log.info("start to delete standard task");
            
            delUrl = "/tasks/standards";
        }

        if (CollectionUtils.isEmpty(thirdTaskIds)) {
            log.info("no thirdTaskIds to delete");
            return;
        }

        JSONObject object = new JSONObject();
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(thirdTaskIds));
        object.put("uuids", array);

        try {
            String rsp = HttpDeleteUtil.sendDelete(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + delUrl,
                    object.toJSONString());
            JSONObject rspObject = JSONObject.parseObject(rsp);
            log.info("delete flow task response: {}", rspObject.toJSONString());

            if (ResponseUtils.checkResponse(rspObject)) {
                log.info("delete flow task success");
            } else {
                log.error("delete flow task response error");
            }

            
            this.delPorts(flowActuatorModel, flowPortIds);

            
            flowPortMapper.bulkDelete(flowPortIds.toArray(new String[0]));
        } catch (Exception e) {
            log.error("delete task from flow actuator platform error, {}", ExceptionUtils.getStackTrace(e));
        }
    }

    
    public String addExecutorSync(String trexName, List<FlowNetworkInfo> networkInfos) {
        String uuid = TextUtils.uuid();
        this.addFlowExecutor(uuid, trexName, networkInfos);
        return uuid;
    }

    
    public String syncStartExecutor(String trexName) {
        String uuid = TextUtils.uuid();
        this.startExecutor(uuid, trexName);
        return uuid;
    }

    
    @Async
    public void addExecutor(String uuid, List<FlowNetworkInfo> networkInfos) {
        this.addFlowExecutor(uuid, String.valueOf(System.currentTimeMillis()), networkInfos);
    }

    
    private void addFlowExecutor(String uuid, String trexName, List<FlowNetworkInfo> networkInfos) {
        log.info("addFlowExecutor networkInfos:{}", networkInfos);
        
        String externalNetId = "";
        try {
            String getNetworkRsp = HttpUtils.sendPost(flowConfig.getCpUrl() + "/v1/scenarios/virtual_internet", new JSONObject().toJSONString());
            JSONObject getNetworkObj = JSONObject.parseObject(getNetworkRsp);
            log.info("get network response:{}", getNetworkObj.toJSONString());
            if (BaseConstants.SUCCESS_ZERO.equals(getNetworkObj.getString(BaseConstants.STR_CODE))) {
                JSONObject networkObj = getNetworkObj.getJSONObject(BaseConstants.STR_DATA);
                externalNetId = networkObj.getString("external_net_id");
            }
        } catch (IOException e) {
            log.error("get network error,{}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(FlowErrorCode.GET_OUTER_NETWORK_ERROR);
        }

        FlowActuatorModel flowActuatorModel = new FlowActuatorModel();
        AddVmRequest addVmRequest = new AddVmRequest();
        addVmRequest.setName(trexName);
        addVmRequest.setImage(flowConfig.getTrexImage());
        AddVmRequest.Networks networks = new AddVmRequest.Networks();
        networks.setNetworkId(externalNetId);
        AddVmRequest.AccessModes accessModes = new AddVmRequest.AccessModes();
        accessModes.setPort(80);
        accessModes.setEnablePortMapping(false);
        networks.setAccessModes(Collections.singletonList(accessModes));
        addVmRequest.setNetworks(Collections.singletonList(networks));

        try {
            
            String addVmRsp = HttpUtils.sendPost(flowConfig.getCpUrl() + "/v1/scenarios/vm",
                    JsonUtils.snakeStringify(addVmRequest));
            JSONObject addVmObj = JSONObject.parseObject(addVmRsp);
            log.info("add trex vm response:{}", addVmObj.toJSONString());

            String fixedIp = null;
            if (BaseConstants.SUCCESS_ZERO.equals(addVmObj.getString(BaseConstants.STR_CODE))) {
                JSONObject vmObj = addVmObj.getJSONObject(BaseConstants.STR_DATA);
                String instanceId = vmObj.getString("id");
                JSONObject netWorkObj = vmObj.getJSONArray("networks").getJSONObject(0);
                fixedIp = netWorkObj.getString("fixed_ip");
                String ip = fixedIp + BaseConstants.COLON + "80";

                String clientMac = netWorkObj.getString("mac_address");
                String clientIp = fixedIp;
                String clientPortId = netWorkObj.getString("port_id");

                
                String serverMac = clientMac;
                String serverIp = clientIp;
                String serverPortId = clientPortId;

                
                flowActuatorModel.setId(uuid);
                flowActuatorModel.setTitle(addVmRequest.getName());
                flowActuatorModel.setInstanceId(instanceId);
                flowActuatorModel.setIp(ip);
                flowActuatorModel.setNetworkId(externalNetId);
                flowActuatorMapper.create(flowActuatorModel);

                
                FlowPortModel flowPortModel = FlowPortModel.builder()
                        .serverIp(serverIp)
                        .serverMac(serverMac)
                        .serverPortId(serverPortId)
                        .serverGateway(fixedIp) 
                        .clientIp(clientIp)
                        .clientMac(clientMac)
                        .clientPortId(clientPortId)
                        .clientGateway(fixedIp) 
                        .networkId(externalNetId)
                        .actuatorId(flowActuatorModel.getId())
                        .portType(PortTypeEnum.SCRIPT.toValue())
                        .build();
                flowPortMapper.create(flowPortModel);

                String extNetId = externalNetId;
                
                String finalFixedIp = fixedIp;
                NetworkUtils.probePort(fixedIp, 22, 1000, 1000, 1000 * 60 * 10,
                        null, 0, () -> checkTrexServer(finalFixedIp, networkInfos, flowActuatorModel, extNetId)
                        , () -> log.error("Trex vm start error:{}", addVmRequest.getName()));

            } else {
                log.error("add trex vm error");
                throw new MpRuntimeException(addVmObj.getString(BaseConstants.STR_DETAIL));
            }
        } catch (MpRuntimeException e) {
            log.error("add vm instance error, {}", ExceptionUtils.getStackTrace(e));
            throw e;
        } catch (Exception e) {
            log.error("add vm instance error, {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(FlowErrorCode.ADD_FLOW_EXECUTOR_ERROR);
        }
    }


    
    private void startExecutor(String uuid, String trexName) {
        log.info("addFlowExecutor name:{}", trexName);
        
        String externalNetId = "";
        try {
            String getNetworkRsp = HttpUtils.sendPost(flowConfig.getCpUrl() + "/v1/scenarios/virtual_internet", new JSONObject().toJSONString());
            JSONObject getNetworkObj = JSONObject.parseObject(getNetworkRsp);
            log.info("get network response:{}", getNetworkObj.toJSONString());
            if (BaseConstants.SUCCESS_ZERO.equals(getNetworkObj.getString(BaseConstants.STR_CODE))) {
                JSONObject networkObj = getNetworkObj.getJSONObject(BaseConstants.STR_DATA);
                externalNetId = networkObj.getString("external_net_id");
            }
        } catch (IOException e) {
            log.error("get network error,{}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(FlowErrorCode.GET_OUTER_NETWORK_ERROR);
        }

        FlowActuatorModel flowActuatorModel = new FlowActuatorModel();
        AddVmRequest addVmRequest = new AddVmRequest();
        addVmRequest.setName(trexName);
        addVmRequest.setImage(flowConfig.getTrexImage());
        AddVmRequest.Networks networks = new AddVmRequest.Networks();
        networks.setNetworkId(externalNetId);
        AddVmRequest.AccessModes accessModes = new AddVmRequest.AccessModes();
        accessModes.setPort(80);
        accessModes.setEnablePortMapping(false);
        networks.setAccessModes(Collections.singletonList(accessModes));
        addVmRequest.setNetworks(Collections.singletonList(networks));

        try {
            
            String addVmRsp = HttpUtils.sendPost(flowConfig.getCpUrl() + "/v1/scenarios/vm",
                    JsonUtils.snakeStringify(addVmRequest));
            JSONObject addVmObj = JSONObject.parseObject(addVmRsp);
            log.info("add trex vm response:{}", addVmObj.toJSONString());

            String fixedIp = null;
            if (BaseConstants.SUCCESS_ZERO.equals(addVmObj.getString(BaseConstants.STR_CODE))) {
                JSONObject vmObj = addVmObj.getJSONObject(BaseConstants.STR_DATA);
                String instanceId = vmObj.getString("id");
                JSONObject netWorkObj = vmObj.getJSONArray("networks").getJSONObject(0);
                fixedIp = netWorkObj.getString("fixed_ip");
                String ip = fixedIp + BaseConstants.COLON + "80";

                String mac = netWorkObj.getString("mac_address");

                
                flowActuatorModel.setId(uuid);
                flowActuatorModel.setTitle(addVmRequest.getName());
                flowActuatorModel.setInstanceId(instanceId);
                flowActuatorModel.setIp(ip);
                flowActuatorModel.setNetworkId(externalNetId);
                flowActuatorMapper.create(flowActuatorModel);

                
                FlowTaskPortModel flowTaskPortModel = new FlowTaskPortModel();
                flowTaskPortModel.setActuatorId(uuid);
                flowTaskPortModel.setNetworkId(externalNetId);
                flowTaskPortModel.setMac(mac);
                flowTaskPortService.create(flowTaskPortModel);

                
                String finalFixedIp = fixedIp;
                NetworkUtils.probePort(fixedIp, 22, 1000, 1000, 1000 * 60 * 10,
                        null, 0, () -> checkStartTrex(finalFixedIp),
                        () -> log.error("Trex vm start error:{}", addVmRequest.getName()));

            } else {
                log.error("add trex vm error");
                throw new MpRuntimeException(addVmObj.getString(BaseConstants.STR_DETAIL));
            }
        } catch (MpRuntimeException e) {
            log.error("add vm instance error, {}", ExceptionUtils.getStackTrace(e));
            throw e;
        } catch (Exception e) {
            log.error("add vm instance error, {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(FlowErrorCode.ADD_FLOW_EXECUTOR_ERROR);
        }
    }

    
    public ServiceResult<Boolean> startTask(String taskId, String sourceIp) {
        
        ObjServiceResult<FlowTaskTaskModel> taskServiceResult = flowTaskTaskService.get(taskId);
        if (Objects.isNull(taskServiceResult.getSource()) || Objects.isNull(taskServiceResult.getSource().getObj())) {
            log.error("task is not exist for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }

        
        FlowTaskTaskModel flowTaskTaskModel = taskServiceResult.getSource().getObj();
        if (TaskTaskStatusEnum.RUNNING.toValue() == flowTaskTaskModel.getTaskStatus() || TaskTaskStatusEnum.PREPARING.toValue()== flowTaskTaskModel.getTaskStatus()) {
            log.error("task is running for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_IS_RUNNING);
        }
        if (TaskTaskStatusEnum.INITING.toValue() == flowTaskTaskModel.getTaskStatus()) {
            log.error("task is initing for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_IS_RUNNING);
        }

        
        FlowTaskConfModel flowTaskConfModel = flowTaskConfService.findByTaskId(taskId);
        if (Objects.isNull(flowTaskConfModel)) {
            log.error("task config error for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(flowTaskTaskModel.getActuatorId());
        if (Objects.isNull(flowActuatorModel)) {
            log.error("start task error, flowActuator not exist for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        
        flowTaskRecordService.batchUpdateStatus(0, Collections.singletonList(taskId));

        
        FlowTaskConf flowTaskConf = JSONObject.parseObject(flowTaskConfModel.getConf(), FlowTaskConf.class);
        if (StringUtils.isNotBlank(sourceIp)) {
            
            flowTaskConfModel.setKeepRunning(0);
            
            if ("127.0.0.1".equals(sourceIp)) {
                sourceIp = "";
            }
            FlowTaskFlowConf flowConf = flowTaskConf.getFlowConf();
            if (Objects.nonNull(flowConf)) {
                FlowTaskIpv4Conf ipv4Conf = flowConf.getIpv4Conf();
                if (Objects.nonNull(ipv4Conf)) {
                    ipv4Conf.setSip(sourceIp);
                } else {
                    ipv4Conf = new FlowTaskIpv4Conf();
                    ipv4Conf.setSip(sourceIp);
                    flowConf.setIpv4Conf(ipv4Conf);
                }
            } else {
                flowConf = new FlowTaskFlowConf();
                FlowTaskIpv4Conf ipv4Conf = new FlowTaskIpv4Conf();
                ipv4Conf.setSip(sourceIp);
                flowConf.setIpv4Conf(ipv4Conf);
                flowTaskConf.setFlowConf(flowConf);
            }

            FlowTaskCommonConf commonConf = flowTaskConf.getCommonConf();
            if (Objects.nonNull(commonConf)) {
                commonConf.setIpg(2);
            } else {
                commonConf = new FlowTaskCommonConf();
                commonConf.setIpg(2);
                flowTaskConf.setCommonConf(commonConf);
            }
        }
        
        FlowTaskStart flowTaskStart = FlowTaskStart.builder()
                .taskId(flowTaskTaskModel.getId())
                .flowId(flowTaskTaskModel.getFlowId())
                .resourceType(flowTaskTaskModel.getResourceType())
                .keepRunning(flowTaskConfModel.getKeepRunning())
                .commonConf(flowTaskConf.getCommonConf())
                .rateUnlimited(flowTaskConfModel.getRateUnlimited())
                .rateConf(flowTaskConf.getRateConf())
                .replay(flowTaskConfModel.getReplay())
                .flowConf(flowTaskConf.getFlowConf())
                .attackConfs(flowTaskConf.getAttackConfs())
                .build();
        StartFlowTaskRequest startFlowTaskRequest = StartFlowTaskRequest.builder()
                .action(TaskActionEnum.START.toValue())
                .time(TimeUtils.localDateTimeToString(LocalDateTime.now()))
                .task(flowTaskStart)
                .build();

        try {
            String startTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/v2/tasks/action",
                    JsonUtils.snakeStringify(startFlowTaskRequest));
            JSONObject startTaskObj = JSONObject.parseObject(startTaskRsp);
            log.info("start task response:{}", startTaskObj.toJSONString());
            if (ResponseUtils.checkResponse(startTaskObj)) {
                log.info("start task success for {}", taskId);

                
                FlowWebSocketClientUtil wsClient = FlowWebSocketClientUtil.getWebSocketClient(flowActuatorModel.getIp().split(BaseConstants.COLON)[0]);
                wsClient.sendMessage(taskId);

                
                flowTaskTaskService.updateTaskStatus(taskId, TaskStatusEnum.RUNNING.toValue());
                return ResultCommon.success(true);
            } else {
                if (ResponseUtils.checkRunning(startTaskObj)) {
                    
                    flowTaskTaskService.updateTaskStatus(taskId, TaskStatusEnum.RUNNING.toValue());
                    return ResultCommon.success(true);
                }
                log.error("start task failed for {}", taskId);
                return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
            }
        } catch (Exception e) {
            log.error("start task error for {}", taskId, e);
            if (ExceptionUtils.getStackTrace(e).contains("504 Gateway Time-out")) {
                reloadTrex(flowActuatorModel.getIp().split(BaseConstants.COLON)[0]);
            }
            return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }
    }

    
    public ServiceResult<Boolean> batchStartTasks(List<String> taskIds, BatchStartTaskBO batchStartTaskBO) {
        Integer runType = null;
        if (Objects.nonNull(batchStartTaskBO)) {
            
            runType = batchStartTaskBO.getRunType();
            taskIds = batchStartTaskBO.getTaskSipList().stream().map(TaskSipBO::getTaskId).collect(Collectors.toList());
        }

        
        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listByTaskIds(taskIds);
        if (CollectionUtils.isEmpty(flowTaskTaskModels) || flowTaskTaskModels.size() != taskIds.size()) {
            log.error("tasks are not all exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }

        
        List<String> notRunningTaskIds = new ArrayList<>();
        List<FlowTaskTaskModel> notRunningTasks = flowTaskTaskModels.stream().filter(flowTaskTaskModel ->
                TaskTaskStatusEnum.RUNNING.toValue() != flowTaskTaskModel.getTaskStatus() && TaskTaskStatusEnum.INITING.toValue() != flowTaskTaskModel.getTaskStatus() && TaskTaskStatusEnum.PREPARING.toValue() != flowTaskTaskModel.getTaskStatus()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(notRunningTasks)) {
            log.error("some tasks are already running");
            throw new MpRuntimeException(FlowErrorCode.TASK_IS_RUNNING);
        } else {
            notRunningTaskIds = notRunningTasks.stream().map(FlowTaskTaskModel::getId).collect(Collectors.toList());
            flowTaskTaskModels = flowTaskTaskService.listByTaskIds(notRunningTaskIds);
        }

        
        List<FlowTaskConfModel> flowTaskConfModels = flowTaskConfService.listByTaskIds(notRunningTaskIds);
        if (CollectionUtils.isEmpty(flowTaskConfModels) || flowTaskConfModels.size() != notRunningTaskIds.size()) {
            log.error("not all tasks config exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(flowTaskTaskModels.get(0).getActuatorId());
        if (Objects.isNull(flowActuatorModel)) {
            log.error("batch start tasks error, flowActuator not exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        
        flowTaskRecordService.batchUpdateStatus(0, taskIds);

        List<FlowTaskStart> flowTaskStartList = new ArrayList<>();
        flowTaskTaskModels.forEach(flowTaskTaskModel -> {
            
            FlowTaskConfModel flowTaskConfModel = flowTaskConfModels.stream().filter(taskConfModel ->
                    taskConfModel.getTaskId().equals(flowTaskTaskModel.getId())).findFirst().get();
            FlowTaskConf flowTaskConf = JSONObject.parseObject(flowTaskConfModel.getConf(), FlowTaskConf.class);

            if (Objects.nonNull(batchStartTaskBO)) {
                
                flowTaskConfModel.setKeepRunning(0);
                TaskSipBO sipBO = batchStartTaskBO.getTaskSipList().stream().filter(taskSipBO ->
                        flowTaskTaskModel.getId().equals(taskSipBO.getTaskId())).findFirst().get();

                
                String sourceIp = sipBO.getSourceIp();
                FlowTaskFlowConf flowConf = flowTaskConf.getFlowConf();
                if (Objects.nonNull(flowConf)) {
                    FlowTaskIpv4Conf ipv4Conf = flowConf.getIpv4Conf();
                    if (Objects.nonNull(ipv4Conf)) {
                        ipv4Conf.setSip(sourceIp);
                    } else {
                        ipv4Conf = new FlowTaskIpv4Conf();
                        ipv4Conf.setSip(sourceIp);
                        flowConf.setIpv4Conf(ipv4Conf);
                    }
                } else {
                    flowConf = new FlowTaskFlowConf();
                    FlowTaskIpv4Conf ipv4Conf = new FlowTaskIpv4Conf();
                    ipv4Conf.setSip(sourceIp);
                    flowConf.setIpv4Conf(ipv4Conf);
                    flowTaskConf.setFlowConf(flowConf);
                }

                FlowTaskCommonConf commonConf = flowTaskConf.getCommonConf();
                if (Objects.nonNull(commonConf)) {
                    commonConf.setLatency(2);
                } else {
                    commonConf = new FlowTaskCommonConf();
                    commonConf.setLatency(2);
                    flowTaskConf.setCommonConf(commonConf);
                }
            }

            
            FlowTaskStart flowTaskStart = FlowTaskStart.builder()
                    .taskId(flowTaskTaskModel.getId())
                    .flowId(flowTaskTaskModel.getFlowId())
                    .resourceType(flowTaskTaskModel.getResourceType())
                    .keepRunning(flowTaskConfModel.getKeepRunning())
                    .commonConf(flowTaskConf.getCommonConf())
                    .rateUnlimited(flowTaskConfModel.getRateUnlimited())
                    .rateConf(flowTaskConf.getRateConf())
                    .replay(flowTaskConfModel.getReplay())
                    .flowConf(flowTaskConf.getFlowConf())
                    .attackConfs(flowTaskConf.getAttackConfs())
                    .build();
            flowTaskStartList.add(flowTaskStart);
        });

        BatchStartFlowTaskRequest batchStartFlowTaskRequest = BatchStartFlowTaskRequest.builder()
                .action(TaskActionEnum.START.toValue())
                .time(TimeUtils.localDateTimeToString(LocalDateTime.now()))
                .tasks(flowTaskStartList)
                .runType(runType)
                .build();

        try {
            String batchStartTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/v2/tasks/batch/action",
                    JsonUtils.snakeStringify(batchStartFlowTaskRequest));
            JSONObject batchStartTaskObj = JSONObject.parseObject(batchStartTaskRsp);
            log.info("batch start task response:{}", batchStartTaskObj.toJSONString());
            if (ResponseUtils.checkResponse(batchStartTaskObj)) {
                log.info("batch start task success");

                
                FlowWebSocketClientUtil wsClient = FlowWebSocketClientUtil.getWebSocketClient(flowActuatorModel.getIp().split(BaseConstants.COLON)[0]);
                wsClient.sendMessage(String.join(BaseConstants.COMMA, notRunningTaskIds));

                
                flowTaskTaskService.batchUpdateTaskStatus(notRunningTaskIds, TaskStatusEnum.RUNNING.toValue());
                return ResultCommon.success(true);
            } else {
                log.error("batch start tasks failed");
                return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
            }
        } catch (Exception e) {
            log.error("batch start tasks error: {}", ExceptionUtils.getStackTrace(e));
            if (ExceptionUtils.getStackTrace(e).contains("504 Gateway Time-out")) {
                reloadTrex(flowActuatorModel.getIp().split(BaseConstants.COLON)[0]);
            }
            return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }
    }

    
    public ServiceResult<Boolean> stopTask(String taskId) {
        log.info("AsyncPlatFlowTaskService.stopTask begin. taskId:{}", taskId);
        
        ObjServiceResult<FlowTaskTaskModel> taskServiceResult = flowTaskTaskService.get(taskId);
        if (Objects.isNull(taskServiceResult.getSource()) || Objects.isNull(taskServiceResult.getSource().getObj())) {
            log.error("task is not exist for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }

        
        FlowTaskTaskModel flowTaskTaskModel = taskServiceResult.getSource().getObj();
        if (TaskStatusEnum.RUNNING.toValue() != flowTaskTaskModel.getTaskStatus()) {
            log.error("task is not running for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.NO_RUNNING_TASK_TO_STOP);
        }

        
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(flowTaskTaskModel.getActuatorId());
        if (Objects.isNull(flowActuatorModel)) {
            log.error("stop task error, flowActuator not exist for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }
        StopFlowTaskRequest stopFlowTaskRequest = StopFlowTaskRequest.builder()
                .action(TaskActionEnum.STOP.toValue())
                .taskId(taskId)
                .build();

        try {
            String stopTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/v2/tasks/action",
                    JsonUtils.snakeStringify(stopFlowTaskRequest));
            JSONObject stopTaskObj = JSONObject.parseObject(stopTaskRsp);
            log.info("stop task response:{}", stopTaskObj.toJSONString());
            if (ResponseUtils.checkResponse(stopTaskObj)) {
                log.info("stop task success for {}", taskId);

                
                flowTaskTaskModel.setTaskStatus(TaskStatusEnum.STOPED.toValue());
                flowTaskTaskModel.setSuccessCount(flowTaskTaskModel.getSuccessCount() + 1);
                flowTaskTaskService.update(flowTaskTaskModel);

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
        } finally {
            
            FlowCacheUtil.taskRunnableCache.invalidate(FlowCacheUtil.RUNNABLE_PREFIX + taskId);
            FlowCacheUtil.taskConsumerCache.invalidate(FlowCacheUtil.CONSUMER_PREFIX + taskId);
        }
    }

    
    public ServiceResult<Boolean> batchStopTasks(List<String> taskIds) {
        log.info("AsyncPlatFlowTaskService.batchStopTasks begin. taskIds:{}", taskIds);
        
        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listByTaskIds(taskIds);
        if (CollectionUtils.isEmpty(flowTaskTaskModels) || flowTaskTaskModels.size() != taskIds.size()) {
            log.error("tasks are not all exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }

        
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(flowTaskTaskModels.get(0).getActuatorId());
        if (Objects.isNull(flowActuatorModel)) {
            log.error("batch stop tasks error, flowActuator not exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        List<String> runningTaskIds = new ArrayList<>();
        
        List<FlowTaskTaskModel> runningTasks = flowTaskTaskModels.stream().filter(flowTaskTaskModel ->
                TaskStatusEnum.RUNNING.toValue() == flowTaskTaskModel.getTaskStatus()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(runningTasks)) {
            log.error("some tasks are not running");
            throw new MpRuntimeException(FlowErrorCode.NO_RUNNING_TASK_TO_STOP);
        } else {
            runningTaskIds = runningTasks.stream().map(FlowTaskTaskModel::getId).collect(Collectors.toList());
        }

        BatchStopFlowTaskRequest batchStopFlowTaskRequest = BatchStopFlowTaskRequest.builder()
                .action(TaskActionEnum.STOP.toValue())
                .taskIds(taskIds)
                .build();

        try {
            String batchStopTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/v2/tasks/batch/action",
                    JsonUtils.snakeStringify(batchStopFlowTaskRequest));
            JSONObject batchStopTaskObj = JSONObject.parseObject(batchStopTaskRsp);
            log.info("batch stop task response:{}", batchStopTaskObj.toJSONString());
            if (ResponseUtils.checkResponse(batchStopTaskObj)) {
                log.info("batch stop task success");

                
                List<FlowTaskTaskModel> runningFlowTaskTaskModels = flowTaskTaskService.listByTaskIds(runningTaskIds);
                for (FlowTaskTaskModel flowTaskTaskModel : runningFlowTaskTaskModels) {
                    flowTaskTaskModel.setTaskStatus(TaskStatusEnum.STOPED.toValue());
                    flowTaskTaskModel.setSuccessCount(flowTaskTaskModel.getSuccessCount() + 1);
                }
                flowTaskTaskService.bulkUpdate(runningFlowTaskTaskModels);


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
        } finally {
            
            FlowCacheUtil.taskConsumerCache.invalidate(FlowCacheUtil.CONSUMER_PREFIX + flowTaskTaskModels.get(0).getSceneId());
        }
    }

    
    private void reloadTrex(String trexIp) {
        log.info("reload trex server");
        String command = "python3 /opt/project_dirs/traffic_generators/utils/trex_reload.py";
        SshUtil.ServerInfo serverInfo = new SshUtil.ServerInfo();
        serverInfo.setHost(trexIp);
        serverInfo.setUser("root");
        serverInfo.setPassword("111111");
        SshUtil.execCommand(serverInfo, command);
    }

    
    private void checkTrexServer(String fixedIp, List<FlowNetworkInfo> networkInfos, FlowActuatorModel flowActuatorModel, String extNetId) {
        String commands = "./init.sh";
        SshUtil.ServerInfo serverInfo = new SshUtil.ServerInfo();
        serverInfo.setHost(fixedIp);
        serverInfo.setUser("root");
        serverInfo.setPassword("111111");
        SshUtil.refreshDir(serverInfo, commands, TGN_PATH, "/opt/project_dirs");

        NetworkUtils.probePort(fixedIp, 5001, 1000, 1000, 1000 * 60 * 5,
                null, 0, () -> flowPortService.batchCreatePorts(networkInfos, flowActuatorModel,
                        extNetId), () -> log.error("Trex vm start error:{}", fixedIp));
    }

    
    private void checkStartTrex(String fixedIp) {
        String commands = "./init.sh";
        SshUtil.ServerInfo serverInfo = new SshUtil.ServerInfo();
        serverInfo.setHost(fixedIp);
        serverInfo.setUser("root");
        serverInfo.setPassword("111111");
        SshUtil.refreshDir(serverInfo, commands, TGN_PATH, "/opt/project_dirs");

        NetworkUtils.probePort(fixedIp, 5001, 1000, 1000, 1000 * 60 * 5,
                null, 0, null, () -> log.error("Trex vm start error:{}", fixedIp));
    }

    
    private void delPorts(FlowActuatorModel flowActuatorModel, List<String> flowPortIds) {
        List<FlowPortModel> flowPortModels = flowPortMapper.selectByPortIds(flowPortIds);
        flowPortModels.forEach(flowPortModel -> {
            try {
                
                if (StringUtils.isNotBlank(flowPortModel.getServerPortId())) {
                    String delServerPortRsp =
                            HttpUtils.sendDelete(flowConfig.getCpUrl() + "/v1/instances/" + flowActuatorModel.getInstanceId() +
                                    "/interfaces/" + flowPortModel.getServerPortId());
                    JSONObject delServerPortObj = JSONObject.parseObject(delServerPortRsp);
                    log.info("del server port response:{}", delServerPortObj.toJSONString());
                    if (!BaseConstants.SUCCESS_ZERO.equals(delServerPortObj.getString(BaseConstants.STR_CODE))) {
                        log.error("del server port error: {}", flowPortModel.getServerPortId());
                    }
                }

                if (StringUtils.isNotBlank(flowPortModel.getClientPortId())) {
                    String delClientPortRsp =
                            HttpUtils.sendDelete(flowConfig.getCpUrl() + "/v1/instances/" + flowActuatorModel.getInstanceId() +
                                    "/interfaces/" + flowPortModel.getClientPortId());
                    JSONObject delClientPortObj = JSONObject.parseObject(delClientPortRsp);
                    log.info("del client port response:{}", delClientPortObj.toJSONString());
                    if (!BaseConstants.SUCCESS_ZERO.equals(delClientPortObj.getString(BaseConstants.STR_CODE))) {
                        log.error("del client port error: {}", flowPortModel.getClientPortId());
                    }
                }
            } catch (Exception e) {
                log.error("del port error, {}", ExceptionUtils.getStackTrace(e));
            }
        });
    }

    
    public FlowActuatorModel getFlowActuatorModel(String actuatorId) {
        if (StringUtils.isBlank(actuatorId)) {
            log.error("actuatorId in task is empty");
            return null;
        }

        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            log.error("flow actuator is empty for actuatorId:{}", actuatorId);
        }

        return flowActuatorModel;
    }

    
    @Async
    public void deleteFlowFile(List<FlowInfoModel> infoModelList) {
        try {
            if (CollectionUtils.isNotEmpty(infoModelList)) {
                infoModelList.forEach(infoModel -> {
                    String filePath = ResourceTypeEnum.SCRIPT.toValue() == infoModel.getResourceType() ? infoModel.getScriptFile() : infoModel.getPcapFile();
                    FsCommon.delete(filePath);
                });
            }
        } catch (Exception e) {
            log.error("delete flow file error, {}", ExceptionUtils.getStackTrace(e));
        }
    }

    
    public void refreshTaskStatus(String taskId, FlowActuatorModel flowActuatorModel, Runnable callback, Consumer<String> errorCallback) {
        String host = flowActuatorModel.getIp();
        FlowTaskModel flowTaskModel = flowTaskMapper.get(taskId);
        if (Objects.isNull(flowTaskModel)) {
            log.error("flowTaskModel is empty for [{}]", taskId);
            return;
        }

        
        CheckUtils.loopCheck(() -> checkTaskJobStopped(flowTaskModel, host, errorCallback), 2000, 1000 * 60 * 60 * 24,
                () -> refreshTaskStatusStopCheck(flowTaskModel.getId()), 1, () -> taskJobStoppedCallback(flowTaskModel.getId(), callback), () -> log.error("task job running status get failed：[{}]", taskId), "CheckTaskJobStopped");
    }


    
    public void batchRefreshTaskStatus(List<FlowTaskModel> flowTaskModels, FlowActuatorModel flowActuatorModel, Consumer<BatchActuatorCallbackResponse> consumer) {
        String host = flowActuatorModel.getIp();
        
        CheckUtils.loopCheck(() -> batchCheckTaskJobStopped(flowTaskModels, host, consumer), 2000, 1000 * 60 * 60,
                null, 1, null, () -> log.error("batch get task job running status failed"), "BatchCheckTaskJobStopped");

    }

    
    private Boolean refreshTaskStatusStopCheck(String taskId) {
        FlowTaskModel flowTaskModel = flowTaskMapper.get(taskId);
        if (Objects.isNull(flowTaskModel)) {
            log.error("flowTaskModel is empty for [{}]", taskId);
            return false;
        }

        return TaskStatusEnum.STOPED.toValue() == flowTaskModel.getTaskStatus();
    }

    
    private void taskJobStoppedCallback(String taskId, Runnable call) {
        FlowTaskModel oldFlowTaskModel = flowTaskMapper.get(taskId);
        if (Objects.isNull(oldFlowTaskModel)) {
            log.info("flowTaskModel is empty for taskId:{}", taskId);
            return;
        }

        FlowTaskModel flowTaskModel = new FlowTaskModel();
        flowTaskModel.setId(taskId);
        flowTaskModel.setTaskStatus(TaskStatusEnum.STOPED.toValue());
        flowTaskModel.setUpdateTime(LocalDateTime.now());

        
        flowTaskMapper.updateTaskInfo(flowTaskModel);

        
        if (Objects.nonNull(call)) {
            log.info("start to run callback function");
            call.run();
        }

        if (Objects.equals(CustomFlagEnum.YES.toValue(), oldFlowTaskModel.getCustomFlag())) {
            
            String actuatorId = oldFlowTaskModel.getActuatorId();
            FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
            if (Objects.isNull(flowActuatorModel)) {
                log.error("del ports failed for actuator is empty");
                return;
            }

            List<String> flowPortIds = Collections.singletonList(oldFlowTaskModel.getFlowPortId());
            this.delPorts(flowActuatorModel, flowPortIds);

            flowTaskModel.setFlowPortId(Strings.EMPTY);
            
            flowTaskMapper.updateTaskInfo(flowTaskModel);
        }

    }

    
    private void jobStoppedCallback(String taskId, Consumer<String> call, String errMsg) {
        FlowTaskModel oldFlowTaskModel = flowTaskMapper.get(taskId);
        if (Objects.isNull(oldFlowTaskModel)) {
            log.info("flowTaskModel is empty for taskId:{}", taskId);
            return;
        }

        FlowTaskModel flowTaskModel = new FlowTaskModel();
        flowTaskModel.setId(taskId);
        flowTaskModel.setTaskStatus(TaskStatusEnum.STOPED.toValue());
        flowTaskModel.setUpdateTime(LocalDateTime.now());

        
        flowTaskMapper.updateTaskInfo(flowTaskModel);

        
        if (Objects.nonNull(call)) {
            log.info("start to run callback function");
            call.accept(errMsg);
        }

        if (Objects.equals(CustomFlagEnum.YES.toValue(), oldFlowTaskModel.getCustomFlag())) {
            
            String actuatorId = oldFlowTaskModel.getActuatorId();
            FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
            if (Objects.isNull(flowActuatorModel)) {
                log.error("del ports failed for actuator is empty");
                return;
            }

            List<String> flowPortIds = Collections.singletonList(oldFlowTaskModel.getFlowPortId());
            this.delPorts(flowActuatorModel, flowPortIds);

            flowTaskModel.setFlowPortId(Strings.EMPTY);
            
            flowTaskMapper.updateTaskInfo(flowTaskModel);
        }

    }

    
    private Boolean checkTaskJobStopped(FlowTaskModel flowTaskModel, String hostUrl, Consumer<String> errorCallback) {
        Boolean result = false;
        if (StringUtils.isBlank(flowTaskModel.getThirdTaskId())) {
            log.warn("no thirdTaskIds to query");
            return result;
        }

        if (StringUtils.isBlank(hostUrl)) {
            log.error("hostUrl is empty");
            return result;
        }

        try {
            JSONObject statusObj = new JSONObject();
            JSONArray statusArray = new JSONArray();
            statusArray.addAll(Collections.singletonList(flowTaskModel.getThirdTaskId()));
            statusObj.put("uuids", statusArray);

            String statusRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(hostUrl) + "/tasks/standards/many",
                    statusObj.toJSONString());
            JSONObject statusRspObj = JSONObject.parseObject(statusRsp);
            log.info("query task status response:{}", statusRspObj.toJSONString());
            if (ResponseUtils.checkResponse(statusRspObj)) {
                JSONArray rows = statusRspObj.getJSONObject(BaseConstants.STR_DATA).getJSONArray("rows");
                if (Objects.nonNull(rows)) {
                    Integer taskStatus = rows.getJSONObject(0).getInteger("status");
                    
                    if (TaskStatusEnum.STOPED.toValue() == taskStatus || TaskStatusEnum.STOPING.toValue() == taskStatus) {
                        
                        Integer isSuccess = rows.getJSONObject(0).getInteger("result");
                        if (Objects.nonNull(isSuccess) && 1 == isSuccess) {
                            log.info("flow execute success:[{}]", flowTaskModel.getId());
                            result = true;
                        } else {
                            FlowInfoModel flowInfoModel = flowInfoMapper.get(flowTaskModel.getFlowId());
                            
                            String errMsg = rows.getJSONObject(0).getString("result_detail");
                            if (Objects.nonNull(flowInfoModel) && flowInfoModel.getResourceType() == ResourceTypeEnum.SCRIPT.toValue()) {
                                
                                errMsg = MessageCommonUtil.convertMessage("scriptFlowError").concat(errMsg).replace("\\n\'", "");
                            }
                            log.error("flow [{}] execute error for: {}", flowTaskModel.getId(), errMsg);
                            jobStoppedCallback(flowTaskModel.getId(), errorCallback, errMsg);
                        }

                    }
                }
            } else {
                log.error("query task status failed");
            }
        } catch (Exception e) {
            log.error("query task status error", e);
        }

        return result;
    }

    
    private Boolean batchCheckTaskJobStopped(List<FlowTaskModel> flowTaskModels, String hostUrl, Consumer<BatchActuatorCallbackResponse> consumer) {
        if (StringUtils.isBlank(hostUrl)) {
            log.error("hostUrl is empty");
            return false;
        }

        List<String> taskIds = flowTaskModels.stream().map(FlowTaskModel::getId).collect(Collectors.toList());
        List<String> thirdTaskIds = flowTaskModels.stream().map(FlowTaskModel::getThirdTaskId).collect(Collectors.toList());
        try {
            JSONObject statusObj = new JSONObject();
            JSONArray statusArray = new JSONArray();
            statusArray.addAll(thirdTaskIds);
            statusObj.put("uuids", statusArray);

            String statusRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(hostUrl) + "/tasks/standards/many",
                    statusObj.toJSONString());
            JSONObject statusRspObj = JSONObject.parseObject(statusRsp);
            log.info("batch query task status response:{}", statusRspObj.toJSONString());
            if (ResponseUtils.checkResponse(statusRspObj)) {
                JSONArray rows = statusRspObj.getJSONObject(BaseConstants.STR_DATA).getJSONArray("rows");
                if (Objects.nonNull(rows)) {
                    
                    long count = rows.stream().filter(row ->
                            TaskStatusEnum.INIT.toValue() == ((JSONObject) row).getInteger("status")
                                    || TaskStatusEnum.RUNNING.toValue() == ((JSONObject) row).getInteger("status")).count();
                    if (count > 0) {
                        log.info("batch actuator task is still running for hostUrl: {}", hostUrl);
                        return false;
                    }

                    List<FlowTaskModel> successTaskModels = new ArrayList<>();
                    List<FlowTaskModel> errorTaskModels = new ArrayList<>();
                    rows.forEach(row -> {
                        JSONObject resultObj = (JSONObject) row;
                        String thirdTaskId = resultObj.getString("uuid");
                        FlowTaskModel taskModel = flowTaskModels.stream().filter(task -> Objects.equals(task.getThirdTaskId(), thirdTaskId)).findFirst().get();

                        
                        Integer isSuccess = resultObj.getInteger("result");
                        if (Objects.nonNull(isSuccess) && 1 == isSuccess) {
                            log.info("flow execute success:[{}]", taskModel.getId());
                            successTaskModels.add(taskModel);
                        } else {
                            
                            log.info("flow execute error:[{}]", taskModel.getId());
                            errorTaskModels.add(taskModel);
                        }
                    });

                    
                    flowTaskMapper.batchUpdateTaskStatus(TaskStatusEnum.STOPED.toValue(), null, taskIds);

                    if (Objects.nonNull(consumer)) {
                        BatchActuatorCallbackResponse callbackResponse = new BatchActuatorCallbackResponse();
                        callbackResponse.setSuccessTaskModels(successTaskModels);
                        callbackResponse.setErrorTaskModels(errorTaskModels);
                        consumer.accept(callbackResponse);
                    }

                    return true;
                }
            } else {
                log.error("batch query task status failed");
            }
        } catch (Exception e) {
            log.error("batch query task status error", e);
        }

        return false;
    }


    
    public boolean uploadFlowInfo(FlowInfoModel flowInfoModel, FlowTaskModel flowTaskModel, AddFlowTaskRequest addFlowTaskRequest) {
        if (Objects.isNull(flowInfoModel) || Objects.isNull(flowTaskModel)) {
            return false;
        }

        boolean isSuccess = true;
        try {
            String actuatorId = flowTaskModel.getActuatorId();
            FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
            String resourceUrl = CommonUtil.getResourcetUrl(flowActuatorModel.getIp());

            
            if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
                
                byte[] fileByte = FsCommon.downloadRawContent(flowInfoModel.getPcapFile());
                
                JSONObject object = new JSONObject();
                object.put("fileName",
                        flowInfoModel.getTitle() + System.currentTimeMillis() + "." + flowInfoModel.getPcapFile().split(BaseConstants.SPOT)[1]);
                String pcapRsp = HttpUtils.postFile(resourceUrl + "/pcap/import", fileByte, "file", object);
                JSONObject jsonObject = JSONObject.parseObject(pcapRsp);
                log.info("upload pcap file response:{}", jsonObject.toJSONString());

                if (ResponseUtils.checkResponse(jsonObject)) {
                    
                    String fileName = jsonObject.getJSONObject(BaseConstants.STR_DATA).getString("file_name");
                    flowTaskModel.setThirdFlowName(fileName);

                    
                    AddPcapRequest addPcapRequest = new AddPcapRequest();
                    addPcapRequest.setFileName(fileName);
                    String addPcapRsp = HttpUtils.sendPost(resourceUrl + "/pcap",
                            JsonUtils.snakeStringify(addPcapRequest));

                    JSONObject addPcapObj = JSONObject.parseObject(addPcapRsp);
                    log.info("add pcap file response:{}", addPcapObj.toJSONString());
                    if (ResponseUtils.checkResponse(addPcapObj)) {
                        
                        String uuid = addPcapObj.getJSONObject(BaseConstants.STR_DATA).getString("uuid");
                        flowTaskModel.setThirdFlowId(uuid);
                    } else {
                        log.error("add pcap file error");
                        isSuccess = false;
                    }
                } else {
                    log.error("upload pcap file error");
                    isSuccess = false;
                }
            } else {
                
                byte[] fileByte = FsCommon.downloadRawContent(flowInfoModel.getScriptFile());
                
                String code = replaceCode(fileByte, flowInfoModel.getId(), addFlowTaskRequest);

                AddScriptsRequest scriptsRequest = new AddScriptsRequest();
                
                String flowName =
                        flowInfoModel.getTitle() + System.currentTimeMillis() + "." + flowInfoModel.getScriptFile().split(BaseConstants.SPOT)[1];
                scriptsRequest.setName(flowName);
                
                String runCmd = "";

                if (flowName.endsWith(".py")) {
                    runCmd = "python " + flowName;
                } else if (flowName.endsWith(".sh")) {
                    runCmd = "bash " + flowName;
                }

                if (StringUtils.isNotBlank(flowInfoModel.getRunCmd())) {
                    runCmd = runCmd + " " + flowInfoModel.getRunCmd();
                }
                scriptsRequest.setRunCmd(runCmd);
                JSONObject object = JSONObject.parseObject(JsonUtils.snakeStringify(scriptsRequest));
                object.put("fileName", flowName);

                
                String scriptRsp = HttpUtils.postFile(resourceUrl + "/scripts", code.getBytes(StandardCharsets.UTF_8), "run_image_script",
                        object);
                JSONObject scriptObj = JSONObject.parseObject(scriptRsp);
                log.info("add script file response:{}", scriptObj.toJSONString());

                if (ResponseUtils.checkResponse(scriptObj)) {
                    
                    String uuid = scriptObj.getJSONObject(BaseConstants.STR_DATA).getString("uuid");
                    flowTaskModel.setThirdFlowId(uuid);
                } else {
                    log.error("add script file error");
                    isSuccess = false;
                }
            }
        } catch (Exception e) {
            log.error("upload file to flow actuator platform error!", e);
            isSuccess = false;
        }

        return isSuccess;
    }

    
    private String replaceCode(byte[] fileByte, String flowId, AddFlowTaskRequest addFlowTaskRequest) {
        String code = new String(fileByte, StandardCharsets.UTF_8);
        List<FlowScriptParamModel> replaceScriptParamModels;
        if (Objects.nonNull(addFlowTaskRequest) && CollectionUtils.isNotEmpty(addFlowTaskRequest.getScriptParamModels())) {
            
            replaceScriptParamModels = addFlowTaskRequest.getScriptParamModels();
            log.info("replace script params:{}", replaceScriptParamModels.toString());
        } else {
            
            replaceScriptParamModels = flowScriptParamService.selectByFlowId(flowId);
            log.info("default script params:{}", replaceScriptParamModels.toString());
        }

        if (CollectionUtils.isNotEmpty(replaceScriptParamModels)) {
            
            for (FlowScriptParamModel replaceScriptParamModel : replaceScriptParamModels) {
                if (ParamTypeEnum.IP.toString().equalsIgnoreCase(replaceScriptParamModel.getParamKey())) {
                    
                    code = code.replace("$" + ParamTypeEnum.IP.name().toUpperCase(), replaceScriptParamModel.getParamValue());
                    code = code.replace("$" + ParamTypeEnum.IP.name().toLowerCase(), replaceScriptParamModel.getParamValue());
                } else {
                    code = code.replace("$" + replaceScriptParamModel.getParamKey(), replaceScriptParamModel.getParamValue());
                }
            }
        }

        return code;
    }


}
