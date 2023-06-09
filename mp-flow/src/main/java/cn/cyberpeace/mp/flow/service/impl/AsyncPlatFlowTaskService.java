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

/**
 * @Description: 流量任务异步执行服务类
 * @Author qiaokang
 * @Date：2021/8/20 09:56
 */
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

    /**
     * tgn代码目录
     */
    private static final String TGN_PATH = "/root/.mp/resources/flows/traffic_generators.zip";

    /**
     * 保存流量发生平台任务
     *
     * @param request
     * @param isUpdate
     */
//    @Async
    public void savePlatFlowTask(AddFlowTaskRequest request, boolean isUpdate) {
        log.info("start to " + (isUpdate ? "update" : "add") + "platform flow task");

        FlowTaskModel flowTaskModel = request;
        // 流量任务存在校验
        if (Objects.isNull(flowTaskModel)) {
            log.error("flow task not exist");
            return;
        }

        // 流量平台任务是否已创建校验
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

        // 是否自定义流量
        Boolean isCustom = StringUtils.isNotBlank(flowTaskModel.getFlowProtocol());
        FlowInfoModel flowInfoModel = new FlowInfoModel();
        if (!isCustom) {
            flowTaskModel.setCustomFlag(CustomFlagEnum.NO.toValue());
            // 流量信息存在校验
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

        // 流量执行器信息查询
        String actuatorId = flowTaskModel.getActuatorId();
        FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            return;
        }

        // 获取网口信息
        FlowPortModel flowPortModel = null;
        if (isUpdate) {
            // 更新时，查询之前记录
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
            // 构建保存app任务请求
            AddAppTaskRequest appTaskRequest = new AddAppTaskRequest(flowTaskModel, flowPortModel);

            try {
                String saveAppTaskRsp = null;
                if (isUpdate) {
                    // 更新
                    saveAppTaskRsp = HttpUtils.sendPatch(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/app/"
                                    + flowTaskModel.getThirdTaskId(), JsonUtils.snakeStringify(appTaskRequest));
                } else {
                    // 新增
                    saveAppTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/app",
                            JsonUtils.snakeStringify(appTaskRequest));
                }

                JSONObject saveAppTaskObj = JSONObject.parseObject(saveAppTaskRsp);
                log.info("save app task response:{}", saveAppTaskObj.toJSONString());
                if (ResponseUtils.checkResponse(saveAppTaskObj)) {
                    // 第三方任务id
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
                // 封装请求参数
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
                    // 第三方任务id
                    thirdTaskId = saveStandTaskObj.getString(BaseConstants.STR_DATA);
                } else {
                    log.error("save standard task failed");
                    throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
                }
            } catch (IOException e) {
                log.error("save standard task error", e);
            }
        }

        // 更新第三方任务id
        if (StringUtils.isNotBlank(thirdTaskId)) {
            flowTaskModel.setThirdTaskId(thirdTaskId);
            flowTaskMapper.update(flowTaskModel);
        }

    }

    /**
     * 批量新增流量任务
     * @param addTaskRequestList
     * @return
     */
    public List<FlowTaskModel> batchSavePlatFlowTask(List<AddFlowTaskRequest> addTaskRequestList) {
        log.info("start to batch save platform flow task");
        List<FlowTaskModel> flowTaskModelList = new ArrayList<>();
        if (CollectionUtils.isEmpty(addTaskRequestList)) {
            log.error("batch save task request list is empty");
            return flowTaskModelList;
        }

        // 遍历封装所有流量任务
        for (AddFlowTaskRequest request : addTaskRequestList) {
            FlowTaskModel flowTaskModel = request;
            flowTaskModelList.add(flowTaskModel);
            // 流量任务存在校验
            if (Objects.isNull(flowTaskModel)) {
                log.error("flow task not exist");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }

            // 流量平台任务是否已创建校验
            if (StringUtils.isNotBlank(flowTaskModel.getId())) {
                log.info("third flow task is already exist");
                continue;
            }

            FlowInfoModel flowInfoModel = new FlowInfoModel();
            // 流量信息存在校验
            flowInfoModel = flowInfoMapper.get(flowTaskModel.getFlowId());
            if (Objects.isNull(flowInfoModel)) {
                log.error("flow info in task not exist:{}", flowTaskModel.getFlowId());
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }

            // 上传流量文件
            if (!uploadFlowInfo(flowInfoModel, flowTaskModel, request)) {
                log.error("upload flow file info error");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }

            // 流量执行器信息查询
            String actuatorId = flowTaskModel.getActuatorId();
            FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
            if (Objects.isNull(flowActuatorModel)) {
                log.error("flowActuatorModel is empty");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }

            String thirdTaskId = "";
            // 获取网口信息
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
            // 设置网口id
            flowTaskModel.setFlowPortId(flowPortModel.getId());

            if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
                // 构建保存背景任务请求
                try {
                    AddAppTaskRequest appTaskRequest = new AddAppTaskRequest(flowTaskModel, flowPortModel);
                    String saveAppTaskRsp = null;
                    // 新增
                    saveAppTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/app",
                            JsonUtils.snakeStringify(appTaskRequest));

                    JSONObject saveAppTaskObj = JSONObject.parseObject(saveAppTaskRsp);
                    log.info("save background task response:{}", saveAppTaskObj.toJSONString());
                    if (ResponseUtils.checkResponse(saveAppTaskObj)) {
                        // 第三方任务id
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
                    // 封装请求参数
                    AddStandardsTaskRequest standTaskRequest = new AddStandardsTaskRequest(flowTaskModel);
                    standTaskRequest.setPort(flowPortModel.getServerMac());
                    String saveStandTaskRsp = null;
                    saveStandTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/standards",
                            JsonUtils.snakeStringify(standTaskRequest));

                    JSONObject saveStandTaskObj = JSONObject.parseObject(saveStandTaskRsp);
                    log.info("save attack task response:{}", saveStandTaskObj.toJSONString());
                    if (ResponseUtils.checkResponse(saveStandTaskObj)) {
                        // 第三方任务id
                        thirdTaskId = saveStandTaskObj.getString(BaseConstants.STR_DATA);
                    } else {
                        log.error("save attack task failed");
                        throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
                    }
                } catch (IOException e) {
                    log.error("save attack task error", e);
                }
            }

            // 更新第三方任务id
            if (StringUtils.isNotBlank(thirdTaskId)) {
                flowTaskModel.setThirdTaskId(thirdTaskId);
            }
            flowTaskMapper.create(flowTaskModel);
        }

        return flowTaskModelList;
    }

    /**
     * 异步删除流量发生平台任务
     *
     * @param resourceType
     * @param flowTaskModels
     */
    @Async
    public void deletePlatFlowTask(int resourceType, List<FlowTaskModel> flowTaskModels) {
        if (CollectionUtils.isEmpty(flowTaskModels)) {
            log.warn("no platform flow task to delete");
            return;
        }

        // 流量执行器信息查询
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
            // 删除pcap包任务地址
            delUrl = "/tasks/app";
        } else {
            log.info("start to delete standard task");
            // 删除脚本文件任务地址
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

            // 删除网口信息
            this.delPorts(flowActuatorModel, flowPortIds);

            // 删除流量网口信息
            flowPortMapper.bulkDelete(flowPortIds.toArray(new String[0]));
        } catch (Exception e) {
            log.error("delete task from flow actuator platform error, {}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 同步新增流量执行器
     * @param trexName
     * @param networkInfos
     * @return
     */
    public String addExecutorSync(String trexName, List<FlowNetworkInfo> networkInfos) {
        String uuid = TextUtils.uuid();
        this.addFlowExecutor(uuid, trexName, networkInfos);
        return uuid;
    }

    /**
     * 同步启动流量执行器
     * @param trexName
     * @return
     */
    public String syncStartExecutor(String trexName) {
        String uuid = TextUtils.uuid();
        this.startExecutor(uuid, trexName);
        return uuid;
    }

    /**
     * 异步新增虚拟机、添加网口
     *
     */
    @Async
    public void addExecutor(String uuid, List<FlowNetworkInfo> networkInfos) {
        this.addFlowExecutor(uuid, String.valueOf(System.currentTimeMillis()), networkInfos);
    }

    /**
     * 新增流量执行器
     * @param uuid
     */
    private void addFlowExecutor(String uuid, String trexName, List<FlowNetworkInfo> networkInfos) {
        log.info("addFlowExecutor networkInfos:{}", networkInfos);
        // 获取外部networkId，用于管理端口
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
            // 创建trex虚拟机
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

                // pcap使用无状态模式，客户端和服务端共用一个网口
                String serverMac = clientMac;
                String serverIp = clientIp;
                String serverPortId = clientPortId;

                // 新增流量执行器信息
                flowActuatorModel.setId(uuid);
                flowActuatorModel.setTitle(addVmRequest.getName());
                flowActuatorModel.setInstanceId(instanceId);
                flowActuatorModel.setIp(ip);
                flowActuatorModel.setNetworkId(externalNetId);
                flowActuatorMapper.create(flowActuatorModel);

                // 添加外网网口信息
                FlowPortModel flowPortModel = FlowPortModel.builder()
                        .serverIp(serverIp)
                        .serverMac(serverMac)
                        .serverPortId(serverPortId)
                        .serverGateway(fixedIp) // Trex的ip作为网关地址
                        .clientIp(clientIp)
                        .clientMac(clientMac)
                        .clientPortId(clientPortId)
                        .clientGateway(fixedIp) // Trex的ip作为网关地址
                        .networkId(externalNetId)
                        .actuatorId(flowActuatorModel.getId())
                        .portType(PortTypeEnum.SCRIPT.toValue())
                        .build();
                flowPortMapper.create(flowPortModel);

                String extNetId = externalNetId;
                // 轮询流量发生器端口，直到端口可访问才返回（轮询10分钟）
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


    /**
     * 启动trex虚拟机
     * @param uuid
     * @param trexName
     */
    private void startExecutor(String uuid, String trexName) {
        log.info("addFlowExecutor name:{}", trexName);
        // 获取外部networkId，用于管理端口
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
            // 创建trex虚拟机
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

                // 新增流量执行器信息
                flowActuatorModel.setId(uuid);
                flowActuatorModel.setTitle(addVmRequest.getName());
                flowActuatorModel.setInstanceId(instanceId);
                flowActuatorModel.setIp(ip);
                flowActuatorModel.setNetworkId(externalNetId);
                flowActuatorMapper.create(flowActuatorModel);

                // 添加流量任务网口信息
                FlowTaskPortModel flowTaskPortModel = new FlowTaskPortModel();
                flowTaskPortModel.setActuatorId(uuid);
                flowTaskPortModel.setNetworkId(externalNetId);
                flowTaskPortModel.setMac(mac);
                flowTaskPortService.create(flowTaskPortModel);

                // 轮询流量发生器端口，直到端口可访问才返回（轮询10分钟）
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

    /**
     * 单个启动流量任务
     * @param taskId
     * @return
     */
    public ServiceResult<Boolean> startTask(String taskId, String sourceIp) {
        // 任务存在校验
        ObjServiceResult<FlowTaskTaskModel> taskServiceResult = flowTaskTaskService.get(taskId);
        if (Objects.isNull(taskServiceResult.getSource()) || Objects.isNull(taskServiceResult.getSource().getObj())) {
            log.error("task is not exist for {}", taskId);
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }

        // 任务状态校验
        FlowTaskTaskModel flowTaskTaskModel = taskServiceResult.getSource().getObj();
        if (TaskStatusEnum.RUNNING.toValue() == flowTaskTaskModel.getTaskStatus() || TaskStatusEnum.STOPING.toValue() == flowTaskTaskModel.getTaskStatus()) {
            log.error("task is running for {}", taskId);
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

        // 解析任务配置
        FlowTaskConf flowTaskConf = JSONObject.parseObject(flowTaskConfModel.getConf(), FlowTaskConf.class);
        if (StringUtils.isNotBlank(sourceIp)) {
            // 有sourceIp，是靶场测评，不支持持续运行
            flowTaskConfModel.setKeepRunning(0);
            // 兼容旧版，有sourceIp设置源ip
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
                commonConf.setLatency(2);
            } else {
                commonConf = new FlowTaskCommonConf();
                commonConf.setLatency(2);
                flowTaskConf.setCommonConf(commonConf);
            }
        }
        // 封装流量任务配置参数
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

                // 跟流量执行器建立ws连接，用于接收任务状态信息
                FlowWebSocketClientUtil wsClient = FlowWebSocketClientUtil.getWebSocketClient(flowActuatorModel.getIp().split(BaseConstants.COLON)[0]);
                wsClient.sendMessage(taskId);

                // 更新任务状态为运行中
                flowTaskTaskService.updateTaskStatus(taskId, TaskStatusEnum.RUNNING.toValue());
                return ResultCommon.success(true);
            } else {
                log.error("start task failed for {}", taskId);
                return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
            }
        } catch (Exception e) {
            log.error("start task error for {}", taskId, e);
            return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }
    }

    /**
     * 批量启动流量任务
     * @param taskIds
     * @return
     */
    public ServiceResult<Boolean> batchStartTasks(List<String> taskIds, BatchStartTaskBO batchStartTaskBO) {
        Integer runType = null;
        if (Objects.nonNull(batchStartTaskBO)) {
            // 有batchStartTaskBO，是靶场测评调用
            runType = batchStartTaskBO.getRunType();
            taskIds = batchStartTaskBO.getTaskSipList().stream().map(TaskSipBO::getTaskId).collect(Collectors.toList());
        }

        // 任务存在校验
        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listByTaskIds(taskIds);
        if (CollectionUtils.isEmpty(flowTaskTaskModels) || flowTaskTaskModels.size() != taskIds.size()) {
            log.error("tasks are not all exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_NOT_EXIST);
        }

        // 任务状态校验
        List<FlowTaskTaskModel> runningTasks = flowTaskTaskModels.stream().filter(flowTaskTaskModel ->
                TaskStatusEnum.RUNNING.toValue() == flowTaskTaskModel.getTaskStatus() || TaskStatusEnum.STOPING.toValue() == flowTaskTaskModel.getTaskStatus()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(runningTasks)) {
            log.error("some tasks are already running");
            throw new MpRuntimeException(FlowErrorCode.TASK_IS_RUNNING);
        }

        // 任务配置校验
        List<FlowTaskConfModel> flowTaskConfModels = flowTaskConfService.listByTaskIds(taskIds);
        if (CollectionUtils.isEmpty(flowTaskConfModels) || flowTaskConfModels.size() != taskIds.size()) {
            log.error("not all tasks config exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        // 流量执行器校验
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(flowTaskTaskModels.get(0).getActuatorId());
        if (Objects.isNull(flowActuatorModel)) {
            log.error("batch start tasks error, flowActuator not exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
        }

        List<FlowTaskStart> flowTaskStartList = new ArrayList<>();
        flowTaskTaskModels.forEach(flowTaskTaskModel -> {
            // 解析任务配置
            FlowTaskConfModel flowTaskConfModel = flowTaskConfModels.stream().filter(taskConfModel ->
                    taskConfModel.getTaskId().equals(flowTaskTaskModel.getId())).findFirst().get();
            FlowTaskConf flowTaskConf = JSONObject.parseObject(flowTaskConfModel.getConf(), FlowTaskConf.class);

            if (Objects.nonNull(batchStartTaskBO)) {
                // 靶场测评，不支持持续运行
                flowTaskConfModel.setKeepRunning(0);
                TaskSipBO sipBO = batchStartTaskBO.getTaskSipList().stream().filter(taskSipBO ->
                        flowTaskTaskModel.getId().equals(taskSipBO.getTaskId())).findFirst().get();

                // 设置源ip
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

            // 封装流量任务配置参数
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

                // 跟流量执行器建立ws连接，用于接收任务状态信息
                FlowWebSocketClientUtil wsClient = FlowWebSocketClientUtil.getWebSocketClient(flowActuatorModel.getIp().split(BaseConstants.COLON)[0]);
                wsClient.sendMessage(String.join(BaseConstants.COMMA, taskIds));

                // 批量更新任务状态为运行中
                flowTaskTaskService.batchUpdateTaskStatus(taskIds, TaskStatusEnum.RUNNING.toValue());
                return ResultCommon.success(true);
            } else {
                log.error("batch start tasks failed");
                return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
            }
        } catch (Exception e) {
            log.error("batch start tasks error");
            return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        }
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

                // 更新任务状态为已停止
                flowTaskTaskService.updateTaskStatus(taskId, TaskStatusEnum.STOPED.toValue());
                log.info("AsyncPlatFlowTaskService.stopTask end.");
                return ResultCommon.success(true);
            } else {
                log.error("stop task failed for {}", taskId);
                return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
            }
        } catch (Exception e) {
            log.error("stop task error for {}", taskId, e);
            return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        } finally {
            // 清除任务回调缓存
            FlowCacheUtil.taskRunnableCache.invalidate(FlowCacheUtil.RUNNABLE_PREFIX + taskId);
            FlowCacheUtil.taskConsumerCache.invalidate(FlowCacheUtil.CONSUMER_PREFIX + taskId);
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

        // 任务状态校验
        List<FlowTaskTaskModel> notRunningTasks = flowTaskTaskModels.stream().filter(flowTaskTaskModel ->
                TaskStatusEnum.RUNNING.toValue() != flowTaskTaskModel.getTaskStatus()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(notRunningTasks)) {
            log.error("some tasks are not running");
            throw new MpRuntimeException(FlowErrorCode.NO_RUNNING_TASK_TO_STOP);
        }

        // 流量执行器校验
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(flowTaskTaskModels.get(0).getActuatorId());
        if (Objects.isNull(flowActuatorModel)) {
            log.error("batch stop tasks error, flowActuator not exist");
            throw new MpRuntimeException(FlowErrorCode.TASK_INFO_ERROR);
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

                // 批量更新任务状态为运行中
                flowTaskTaskService.batchUpdateTaskStatus(taskIds, TaskStatusEnum.STOPED.toValue());

                log.info("AsyncPlatFlowTaskService.batchStopTasks end.");
                return ResultCommon.success(true);
            } else {
                log.error("batch stop tasks failed");
                return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
            }
        } catch (Exception e) {
            log.error("batch stop tasks error");
            return ResultCommon.fail(FlowErrorCode.ACTUATOR_TASK_ERROR);
        } finally {
            // 清除任务回调缓存
            FlowCacheUtil.taskConsumerCache.invalidate(FlowCacheUtil.CONSUMER_PREFIX + flowTaskTaskModels.get(0).getSceneId());
        }
    }

    /**
     * 校验trex服务状态
     * @param fixedIp
     * @param networkInfos
     * @param flowActuatorModel
     * @param extNetId
     */
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

    /**
     * 校验trex启动状态
     * @param fixedIp
     */
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

    /**
     * 删除网口信息
     *
     * @param flowActuatorModel
     * @param flowPortIds
     */
    private void delPorts(FlowActuatorModel flowActuatorModel, List<String> flowPortIds) {
        List<FlowPortModel> flowPortModels = flowPortMapper.selectByPortIds(flowPortIds);
        flowPortModels.forEach(flowPortModel -> {
            try {
                // 删除网口信息
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

    /**
     * 查询执行器信息
     *
     * @param actuatorId
     * @return
     */
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

    /**
     * 异步删除流量文件
     * @param infoModelList
     */
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

    /**
     * 2秒一次轮询任务状态，当任务结束时执行回调方法
     * @param taskId
     * @param flowActuatorModel
     * @param callback
     * @param errorCallback
     * @throws InterruptedException
     */
    public void refreshTaskStatus(String taskId, FlowActuatorModel flowActuatorModel, Runnable callback, Consumer<String> errorCallback) {
        String host = flowActuatorModel.getIp();
        FlowTaskModel flowTaskModel = flowTaskMapper.get(taskId);
        if (Objects.isNull(flowTaskModel)) {
            log.error("flowTaskModel is empty for [{}]", taskId);
            return;
        }

        // 轮询校验任务是否跑完，完成时调用回调方法，最多轮询24小时
        CheckUtils.loopCheck(() -> checkTaskJobStopped(flowTaskModel, host, errorCallback), 2000, 1000 * 60 * 60 * 24,
                () -> refreshTaskStatusStopCheck(flowTaskModel.getId()), 1, () -> taskJobStoppedCallback(flowTaskModel.getId(), callback), () -> log.error("task job running status get failed：[{}]", taskId), "CheckTaskJobStopped");
    }


    /**
     * 2秒一次批量轮询任务状态，当所有任务结束时执行回调方法
     * @param flowTaskModels
     * @param flowActuatorModel
     * @param consumer
     */
    public void batchRefreshTaskStatus(List<FlowTaskModel> flowTaskModels, FlowActuatorModel flowActuatorModel, Consumer<BatchActuatorCallbackResponse> consumer) {
        String host = flowActuatorModel.getIp();
        // 轮询校验批量任务是否跑完，完成时调用回调方法，最多轮询60分钟
        CheckUtils.loopCheck(() -> batchCheckTaskJobStopped(flowTaskModels, host, consumer), 2000, 1000 * 60 * 60,
                null, 1, null, () -> log.error("batch get task job running status failed"), "BatchCheckTaskJobStopped");

    }

    /**
     * 刷新任务状态停止校验方法
     * @param taskId
     * @return
     */
    private Boolean refreshTaskStatusStopCheck(String taskId) {
        FlowTaskModel flowTaskModel = flowTaskMapper.get(taskId);
        if (Objects.isNull(flowTaskModel)) {
            log.error("flowTaskModel is empty for [{}]", taskId);
            return false;
        }

        return TaskStatusEnum.STOPED.toValue() == flowTaskModel.getTaskStatus();
    }

    /**
     * 流量任务停止时回调方法
     * @param taskId
     * @param call
     */
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

        // 更新本地任务状态
        flowTaskMapper.updateTaskInfo(flowTaskModel);

        // 执行回调通知方法
        if (Objects.nonNull(call)) {
            log.info("start to run callback function");
            call.run();
        }

        if (Objects.equals(CustomFlagEnum.YES.toValue(), oldFlowTaskModel.getCustomFlag())) {
            // 自定义流量，运行后直接删除网口
            String actuatorId = oldFlowTaskModel.getActuatorId();
            FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
            if (Objects.isNull(flowActuatorModel)) {
                log.error("del ports failed for actuator is empty");
                return;
            }

            List<String> flowPortIds = Collections.singletonList(oldFlowTaskModel.getFlowPortId());
            this.delPorts(flowActuatorModel, flowPortIds);

            flowTaskModel.setFlowPortId(Strings.EMPTY);
            // 删除任务关联网口
            flowTaskMapper.updateTaskInfo(flowTaskModel);
        }

    }

    /**
     * 流量任务停止时回调方法
     * @param taskId
     * @param call
     */
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

        // 更新本地任务状态
        flowTaskMapper.updateTaskInfo(flowTaskModel);

        // 执行回调通知方法
        if (Objects.nonNull(call)) {
            log.info("start to run callback function");
            call.accept(errMsg);
        }

        if (Objects.equals(CustomFlagEnum.YES.toValue(), oldFlowTaskModel.getCustomFlag())) {
            // 自定义流量，运行后直接删除网口
            String actuatorId = oldFlowTaskModel.getActuatorId();
            FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
            if (Objects.isNull(flowActuatorModel)) {
                log.error("del ports failed for actuator is empty");
                return;
            }

            List<String> flowPortIds = Collections.singletonList(oldFlowTaskModel.getFlowPortId());
            this.delPorts(flowActuatorModel, flowPortIds);

            flowTaskModel.setFlowPortId(Strings.EMPTY);
            // 删除任务关联网口
            flowTaskMapper.updateTaskInfo(flowTaskModel);
        }

    }

    /**
     * 校验任务状态是否已停止
     * @param flowTaskModel
     * @param hostUrl
     * @param errorCallback
     * @return
     */
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
                    // 任务状态为已停止或者停止中，表示任务运行结束
                    if (TaskStatusEnum.STOPED.toValue() == taskStatus || TaskStatusEnum.STOPING.toValue() == taskStatus) {
                        // 判断任务是否运行成功
                        Integer isSuccess = rows.getJSONObject(0).getInteger("result");
                        if (Objects.nonNull(isSuccess) && 1 == isSuccess) {
                            log.info("flow execute success:[{}]", flowTaskModel.getId());
                            result = true;
                        } else {
                            FlowInfoModel flowInfoModel = flowInfoMapper.get(flowTaskModel.getFlowId());
                            // 运行失败，更新任务状态为已停止，调用失败回调方法
                            String errMsg = rows.getJSONObject(0).getString("result_detail");
                            if (Objects.nonNull(flowInfoModel) && flowInfoModel.getResourceType() == ResourceTypeEnum.SCRIPT.toValue()) {
                                // 脚本执行报错，添加前缀
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

    /**
     * 批量校验任务状态是否已停止
     * @param flowTaskModels
     * @param hostUrl
     * @param consumer
     * @return
     */
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
                    // 校验是否有运行中的任务
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

                        // 判断任务是否运行成功
                        Integer isSuccess = resultObj.getInteger("result");
                        if (Objects.nonNull(isSuccess) && 1 == isSuccess) {
                            log.info("flow execute success:[{}]", taskModel.getId());
                            successTaskModels.add(taskModel);
                        } else {
                            // 运行失败，更新任务状态为已停止，调用失败回调方法
                            log.info("flow execute error:[{}]", taskModel.getId());
                            errorTaskModels.add(taskModel);
                        }
                    });

                    // 批量更新流量任务状态为停止
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


    /**
     * 上传流量文件
     *
     * @param flowInfoModel
     * @param flowTaskModel
     * @param addFlowTaskRequest
     * @return
     */
    public boolean uploadFlowInfo(FlowInfoModel flowInfoModel, FlowTaskModel flowTaskModel, AddFlowTaskRequest addFlowTaskRequest) {
        if (Objects.isNull(flowInfoModel) || Objects.isNull(flowTaskModel)) {
            return false;
        }

        boolean isSuccess = true;
        try {
            String actuatorId = flowTaskModel.getActuatorId();
            FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
            String resourceUrl = CommonUtil.getResourcetUrl(flowActuatorModel.getIp());

            // 判断是pcap包还是脚本
            if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
                // 获取pcap流量包
                byte[] fileByte = FsCommon.downloadRawContent(flowInfoModel.getPcapFile());
                // 先向流量发生平台导入pcap包
                JSONObject object = new JSONObject();
                object.put("fileName",
                        flowInfoModel.getTitle() + System.currentTimeMillis() + "." + flowInfoModel.getPcapFile().split(BaseConstants.SPOT)[1]);
                String pcapRsp = HttpUtils.postFile(resourceUrl + "/pcap/import", fileByte, "file", object);
                JSONObject jsonObject = JSONObject.parseObject(pcapRsp);
                log.info("upload pcap file response:{}", jsonObject.toJSONString());

                if (ResponseUtils.checkResponse(jsonObject)) {
                    // 解析返回的名称
                    String fileName = jsonObject.getJSONObject(BaseConstants.STR_DATA).getString("file_name");
                    flowTaskModel.setThirdFlowName(fileName);

                    // 再向流量发生平台创建pcap管理
                    AddPcapRequest addPcapRequest = new AddPcapRequest();
                    addPcapRequest.setFileName(fileName);
                    String addPcapRsp = HttpUtils.sendPost(resourceUrl + "/pcap",
                            JsonUtils.snakeStringify(addPcapRequest));

                    JSONObject addPcapObj = JSONObject.parseObject(addPcapRsp);
                    log.info("add pcap file response:{}", addPcapObj.toJSONString());
                    if (ResponseUtils.checkResponse(addPcapObj)) {
                        // 获取返回的uuid
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
                // 获取script流量脚本
                byte[] fileByte = FsCommon.downloadRawContent(flowInfoModel.getScriptFile());
                // 获取替换脚本参数后的脚本内容
                String code = replaceCode(fileByte, flowInfoModel.getId(), addFlowTaskRequest);

                AddScriptsRequest scriptsRequest = new AddScriptsRequest();
                // 流量执行平台名称不能重复，名称后加上时间戳
                String flowName =
                        flowInfoModel.getTitle() + System.currentTimeMillis() + "." + flowInfoModel.getScriptFile().split(BaseConstants.SPOT)[1];
                scriptsRequest.setName(flowName);
                // 默认执行脚本命令
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

                // 先向流量发生平台新增资源文件
                String scriptRsp = HttpUtils.postFile(resourceUrl + "/scripts", code.getBytes(StandardCharsets.UTF_8), "run_image_script",
                        object);
                JSONObject scriptObj = JSONObject.parseObject(scriptRsp);
                log.info("add script file response:{}", scriptObj.toJSONString());

                if (ResponseUtils.checkResponse(scriptObj)) {
                    // 解析返回的uuid
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

    /**
     * 脚本内容替换
     * @param fileByte
     * @param flowId
     * @param addFlowTaskRequest
     * @return
     */
    private String replaceCode(byte[] fileByte, String flowId, AddFlowTaskRequest addFlowTaskRequest) {
        String code = new String(fileByte, StandardCharsets.UTF_8);
        List<FlowScriptParamModel> replaceScriptParamModels;
        if (Objects.nonNull(addFlowTaskRequest) && CollectionUtils.isNotEmpty(addFlowTaskRequest.getScriptParamModels())) {
            // 有传脚本参数过来，直接用传过来的去替换脚本内容
            replaceScriptParamModels = addFlowTaskRequest.getScriptParamModels();
            log.info("replace script params:{}", replaceScriptParamModels.toString());
        } else {
            // 没有传脚本参数，直接查询默认脚本参数
            replaceScriptParamModels = flowScriptParamService.selectByFlowId(flowId);
            log.info("default script params:{}", replaceScriptParamModels.toString());
        }

        if (CollectionUtils.isNotEmpty(replaceScriptParamModels)) {
            // 根据脚本key替换脚本参数值
            for (FlowScriptParamModel replaceScriptParamModel : replaceScriptParamModels) {
                if (ParamTypeEnum.IP.toString().equalsIgnoreCase(replaceScriptParamModel.getParamKey())) {
                    // ip替换兼容大小写
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
