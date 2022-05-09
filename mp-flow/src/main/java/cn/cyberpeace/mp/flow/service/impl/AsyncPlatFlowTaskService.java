package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.CheckUtils;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.NetworkUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.CustomFlagEnum;
import cn.cyberpeace.mp.flow.enums.FlowPlatformEnum;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.enums.TaskStatusEnum;
import cn.cyberpeace.mp.flow.model.*;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowPortMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.AddAppTaskRequest;
import cn.cyberpeace.mp.flow.request.AddFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.AddStandardsTaskRequest;
import cn.cyberpeace.mp.flow.request.AddVmRequest;
import cn.cyberpeace.mp.flow.service.FlowPortService;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.HttpDeleteUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 流量任务异步执行服务类
 * @Author qiaokang
 * @Date：2021/8/20 09:56
 */
@Service
@Slf4j
public class AsyncPlatFlowTaskService {

    /**
     * 云平台地址
     */
    @Value("${mp.devops.cp-url}")
    private String cpUrl;

    /**
     * trex镜像名称
     */
    @Value("${mp.flow.trex-image}")
    private String trexImage;

    @Autowired
    private FlowTaskMapper flowTaskMapper;

    @Autowired
    private FlowInfoMapper flowInfoMapper;

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    @Autowired
    private FlowPortMapper flowPortMapper;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private FlowPortService flowPortService;

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
        } else {
            // 传入任务所属网络id
            flowActuatorModel.setNetworkId(request.getNetworkId());
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
            // 新增时，根据目标ip查询网口信息
            List<FlowPortModel> flowPortModels =
                    flowPortMapper.list(new HumanCondition().filterEqual(FlowPortModel::getClientGateway,
                    flowTaskModel.getTargetIps().split(BaseConstants.SHORT_DASH)[0])
                    .filterEqual(FlowPortModel::getActuatorId, actuatorId));
            if (CollectionUtils.isEmpty(flowPortModels)) {
                log.error("create flow port info error");
                return;
            }
            flowPortModel = flowPortModels.get(0);
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
            log.error("delete task from flow actuator platform error");
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
        log.info("================ Add vm instance start ==================");
        // 获取外部networkId，用于管理端口
        String externalNetId = "";
        try {
            String getNetworkRsp = HttpUtils.sendPost(cpUrl + "/v1/scenarios/virtual_internet", new JSONObject().toJSONString());
            JSONObject getNetworkObj = JSONObject.parseObject(getNetworkRsp);
            log.info("get network response:{}", getNetworkObj.toJSONString());
            if (BaseConstants.SUCCESS_ZERO.equals(getNetworkObj.getString(BaseConstants.STR_CODE))) {
                JSONObject networkObj = getNetworkObj.getJSONObject(BaseConstants.STR_DATA);
                externalNetId = networkObj.getString("external_net_id");
            }
        } catch (IOException e) {
            log.error("get network error", e);
            throw new MpRuntimeException(FlowErrorCode.GET_OUTER_NETWORK_ERROR);
        }

        FlowActuatorModel flowActuatorModel = new FlowActuatorModel();
        AddVmRequest addVmRequest = new AddVmRequest();
        addVmRequest.setName(FlowPlatformEnum.TREX.toString() + "_" + trexName);
        addVmRequest.setImage(trexImage);
        AddVmRequest.Networks networks = new AddVmRequest.Networks();
        networks.setNetworkId(externalNetId);
        AddVmRequest.AccessModes accessModes = new AddVmRequest.AccessModes();
        accessModes.setPort(80);
        accessModes.setEnablePortMapping(false);
        networks.setAccessModes(Collections.singletonList(accessModes));
        addVmRequest.setNetworks(Collections.singletonList(networks));

        try {
            // 创建trex虚拟机
            String addVmRsp = HttpUtils.sendPost(cpUrl + "/v1/scenarios/vm",
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

                // 新增流量执行器信息
                flowActuatorModel.setId(uuid);
                flowActuatorModel.setTitle(addVmRequest.getName());
                flowActuatorModel.setInstanceId(instanceId);
                flowActuatorModel.setIp(ip);
                flowActuatorMapper.create(flowActuatorModel);

                // 轮询流量发生器端口，直到端口可访问才返回（轮询10分钟）
                NetworkUtils.probePort(fixedIp, 5001, 1000, 1000, 1000 * 60 * 10,
                        null, 0, () -> flowPortService.batchCreatePorts(networkInfos, flowActuatorModel), () -> log.error("Trex vm start error:{}", addVmRequest.getName()));
            } else {
                log.error("add trex vm error");
                throw new MpRuntimeException(addVmObj.getString(BaseConstants.STR_DETAIL));
            }
        } catch (MpRuntimeException e) {
            log.error("add vm instance error", e);
            throw e;
        } catch (Exception e) {
            log.error("add vm instance error", e);
            throw new MpRuntimeException(FlowErrorCode.ADD_FLOW_EXECUTOR_ERROR);
        }

        log.info("================ Add vm instance end ==================");
    }

    /**
     * 获取任务接口状态
     * @param flowActuatorModel
     * @return
     */
    private boolean getTaskJobStatus(FlowActuatorModel flowActuatorModel) {
        boolean isActive = false;
        try {
            JSONObject getTaskObj = new JSONObject();
            JSONArray getTaskArray = new JSONArray();
            getTaskArray.add("test_standardTask");
            getTaskObj.put("uuids", getTaskArray);
            String getTaskRsp = HttpUtils.sendPostWithTimeOut(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/standards/many",
                    getTaskObj.toJSONString(), 10000);
            JSONObject taskResultObj = JSONObject.parseObject(getTaskRsp);
            log.info("get tasks info response:{}", taskResultObj.toJSONString());
            if ("1000".equalsIgnoreCase(taskResultObj.getString("code"))) {
                isActive = true;
            }
        } catch (Exception e) {
            log.error("get tasks info error", e);
        }

        return isActive;
    }

//    /**
//     * 新增网口
//     *
//     * @param flowActuatorModel
//     * @return
//     */
//    private FlowPortModel createPort(FlowActuatorModel flowActuatorModel, int resourceType) {
//        FlowPortModel flowPortModel = null;
//        try {
//            JSONObject addPortReq = new JSONObject();
//            addPortReq.put("network_id", flowActuatorModel.getNetworkId());
//
//            String serverMac = "";
//            String serverIp = "";
//            String serverPortId = "";
//
//            String clientMac = "";
//            String clientIp = "";
//            String clientPortId = "";
//            if (ResourceTypeEnum.PACP.toValue() == resourceType) {
//                // pcap需要给trex虚拟机添加client网口
//                String addTrexClientPortRsp =
//                        HttpUtils.sendPost(cpUrl + "/v1/instances/" + flowActuatorModel.getInstanceId() + "/interfaces"
//                                , addPortReq.toJSONString());
//                JSONObject addTrexClientObj = JSONObject.parseObject(addTrexClientPortRsp);
//                log.info("add trex client port response:{}", addTrexClientObj.toJSONString());
//                if (BaseConstants.SUCCESS_ZERO.equals(addTrexClientObj.getString(BaseConstants.STR_CODE))) {
//                    JSONObject clientDataObj = addTrexClientObj.getJSONObject(BaseConstants.STR_DATA);
//                    clientMac = clientDataObj.getString("mac_address");
//                    clientIp = clientDataObj.getString("fixed_ip");
//                    clientPortId = clientDataObj.getString("port_id");
//
//                    // pcap使用无状态模式，客户端和服务端共用一个网口
//                    serverMac = clientMac;
//                    serverIp = clientIp;
//                    serverPortId = clientPortId;
//                } else {
//                    log.error("add trex client port error");
//                    throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
//                }
//            } else {
//                // 给trex虚拟机添加server网口
//                String addTrexServerPortRsp = HttpUtils.sendPost(cpUrl + "/v1/instances/" +
//                        flowActuatorModel.getInstanceId() + "/interfaces", addPortReq.toJSONString());
//                JSONObject addTrexServerObj = JSONObject.parseObject(addTrexServerPortRsp);
//                log.info("add trex server port response:{}", addTrexServerObj.toJSONString());
//
//                if (BaseConstants.SUCCESS_ZERO.equals(addTrexServerObj.getString(BaseConstants.STR_CODE))) {
//                    JSONObject serverDataObj = addTrexServerObj.getJSONObject(BaseConstants.STR_DATA);
//                    serverMac = serverDataObj.getString("mac_address");
//                    serverIp = serverDataObj.getString("fixed_ip");
//                    serverPortId = serverDataObj.getString("port_id");
//                } else {
//                    log.error("add trex server port error");
//                    throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
//                }
//            }
//
//            flowPortModel = FlowPortModel.builder()
//                    .serverIp(serverIp)
//                    .serverMac(serverMac)
//                    .serverPortId(serverPortId)
//                    .clientIp(clientIp)
//                    .clientMac(clientMac)
//                    .clientPortId(clientPortId)
//                    .build();
//            flowPortMapper.create(flowPortModel);
//        } catch (IOException e) {
//            log.error("add flow port error", e);
//        }
//
//        return flowPortModel;
//    }

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
                            HttpUtils.sendDelete(cpUrl + "/v1/instances/" + flowActuatorModel.getInstanceId() +
                                    "/interfaces/" + flowPortModel.getServerPortId());
                    JSONObject delServerPortObj = JSONObject.parseObject(delServerPortRsp);
                    log.info("del server port response:{}", delServerPortObj.toJSONString());
                    if (!BaseConstants.SUCCESS_ZERO.equals(delServerPortObj.getString(BaseConstants.STR_CODE))) {
                        log.error("del server port error: {}", flowPortModel.getServerPortId());
                    }
                }

                if (StringUtils.isNotBlank(flowPortModel.getClientPortId())) {
                    String delClientPortRsp =
                            HttpUtils.sendDelete(cpUrl + "/v1/instances/" + flowActuatorModel.getInstanceId() +
                                    "/interfaces/" + flowPortModel.getClientPortId());
                    JSONObject delClientPortObj = JSONObject.parseObject(delClientPortRsp);
                    log.info("del client port response:{}", delClientPortObj.toJSONString());
                    if (!BaseConstants.SUCCESS_ZERO.equals(delClientPortObj.getString(BaseConstants.STR_CODE))) {
                        log.error("del client port error: {}", flowPortModel.getClientPortId());
                    }
                }
            } catch (Exception e) {
                log.error("del port error", e);
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
                    fastDFSClient.deleteFile(filePath, FdfsGroupEnum.DEFAULT.toString());
                });
            }
        } catch (Exception e) {
            log.error("delete flow file error", e);
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
    public void refreshTaskStatus(String taskId, FlowActuatorModel flowActuatorModel, Runnable callback, Runnable errorCallback) {
        String host = flowActuatorModel.getIp();
        FlowTaskModel flowTaskModel = flowTaskMapper.get(taskId);
        if (Objects.isNull(flowTaskModel)) {
            log.error("flowTaskModel is empty for [{}]", taskId);
            return;
        }

        // 轮询校验任务是否跑完，完成时调用回调方法，最多轮询20分钟
        CheckUtils.loopCheck(() -> checkTaskJobStopped(flowTaskModel, host, errorCallback), 2000, 1000 * 60 * 20,
                () -> refreshTaskStatusStopCheck(flowTaskModel.getId()), 1, () -> taskJobStoppedCallback(flowTaskModel.getId(), callback), () -> log.error("task job running status get failed：[{}]", taskId), "CheckTaskJobStopped");
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
     * 校验任务状态是否已停止
     * @param flowTaskModel
     * @param hostUrl
     * @param errorCallback
     * @return
     */
    private Boolean checkTaskJobStopped(FlowTaskModel flowTaskModel, String hostUrl, Runnable errorCallback) {
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
                            // 运行失败，更新任务状态为已停止，调用失败回调方法
                            log.info("flow execute error:[{}]", flowTaskModel.getId());
                            taskJobStoppedCallback(flowTaskModel.getId(), errorCallback);
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




}
