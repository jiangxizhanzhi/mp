package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.AddAppTaskRequest;
import cn.cyberpeace.mp.flow.request.AddStandardsTaskRequest;
import cn.cyberpeace.mp.flow.request.AddVmInstanceRequest;
import cn.cyberpeace.mp.flow.request.AddVmRequest;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.HttpDeleteUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 流量任务异步执行服务类
 * @Autho qiaokang
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

    /**
     * 异步保存流量发生平台任务
     *
     * @param flowTaskModel
     * @param isUpdate
     */
    @Async
    public void savePlatFlowTask(FlowTaskModel flowTaskModel, boolean isUpdate) {
        log.info("start to " + (isUpdate ? "update" : "add") + "platform flow task");

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

        // 流量信息存在校验
        FlowInfoModel flowInfoModel = flowInfoMapper.get(flowTaskModel.getFlowId());
        if (Objects.isNull(flowInfoModel)) {
            log.error("flow info in task not exist:{}", flowTaskModel.getFlowId());
            return;
        }

        // 流量执行器信息查询
        String actuatorId = flowTaskModel.getActuatorId();
        FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            return;
        }

        String thirdTaskId = "";

        if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
            // 构建保存app任务请求
            AddAppTaskRequest appTaskRequest = new AddAppTaskRequest(flowTaskModel, flowActuatorModel);

            try {
                String saveAppTaskRsp = null;
                if (isUpdate) {
                    // 更新
                    saveAppTaskRsp = HttpUtils.sendPatch(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks" +
                                    "/app/" + flowTaskModel.getThirdTaskId(),
                            JsonUtils.toUnderlineJSONString(appTaskRequest));
                } else {
                    // 新增
                    saveAppTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/app",
                            JsonUtils.toUnderlineJSONString(appTaskRequest));
                }

                JSONObject saveAppTaskObj = JSONObject.parseObject(saveAppTaskRsp);
                log.info("save app task response:{}", saveAppTaskObj.toJSONString());
                if (ResponseUtils.checkResponse(saveAppTaskObj)) {
                    // 第三方任务id
                    thirdTaskId = saveAppTaskObj.getJSONObject(BaseConstants.STR_DATA).getString("uuid");
                } else {
                    log.error("save app task failed");
                }
            } catch (Exception e) {
                log.error("save app task error", e);
            }
        } else {
            try {
                // 封装请求参数
                AddStandardsTaskRequest standTaskRequest = new AddStandardsTaskRequest(flowTaskModel);
                standTaskRequest.setPort(flowActuatorModel.getClientMac());
                String saveStandTaskRsp = null;
                if (isUpdate) {
                    saveStandTaskRsp = HttpUtils.sendPatch(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks" +
                                    "/standards" + flowTaskModel.getThirdTaskId(),
                            JsonUtils.toUnderlineJSONString(standTaskRequest));
                } else {
                    saveStandTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks" +
                                    "/standards",
                            JsonUtils.toUnderlineJSONString(standTaskRequest));
                }

                JSONObject saveStandTaskObj = JSONObject.parseObject(saveStandTaskRsp);
                log.info("save standard task response:{}", saveStandTaskObj.toJSONString());
                if (ResponseUtils.checkResponse(saveStandTaskObj)) {
                    // 第三方任务id
                    thirdTaskId = saveStandTaskObj.getString(BaseConstants.STR_DATA);
                } else {
                    log.error("save standard task failed");
                }
            } catch (Exception e) {
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
            log.warn("no plat flow task to delete");
            return;
        }

        // 流量执行器信息查询
        String actuatorId = flowTaskModels.get(0).getActuatorId();
        FlowActuatorModel flowActuatorModel = this.getFlowActuatorModel(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            return;
        }

        List<String> thirdTaskIds =
                flowTaskModels.stream().map(FlowTaskModel::getThirdTaskId).collect(Collectors.toList());
        String delUrl = "";
        if (ResourceTypeEnum.PACP.toValue() == resourceType) {
            log.info("start to delete app task");
            // 删除pcap包地址
            delUrl = "/tasks/app";
        } else {
            log.info("start to delete standard task");
            // 删除脚本文件地址
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
        } catch (Exception e) {
            log.error("delete task from flow actuator platform error");
        }
    }

    /**
     * 异步新增虚拟机、添加网口
     *
     * @param request
     * @param uuid
     */
    @Async
    public void addVmInstance(AddVmInstanceRequest request, String uuid) {
        log.info("================ Add vm instance start ==================");
        FlowActuatorModel flowActuatorModel = new FlowActuatorModel();
        AddVmRequest addVmRequest = new AddVmRequest();
        addVmRequest.setName("trex" + System.currentTimeMillis());
        addVmRequest.setImage(trexImage);
        AddVmRequest.Networks networks = new AddVmRequest.Networks();
        networks.setNetworkId(request.getNetworkId());
        AddVmRequest.AccessModes accessModes = new AddVmRequest.AccessModes();
        accessModes.setPort(80);
        accessModes.setEnablePortMapping(true);
        networks.setAccessModes(Collections.singletonList(accessModes));
        addVmRequest.setNetworks(Collections.singletonList(networks));

        try {
            // 创建trex虚拟机
            String addVmRsp = HttpUtils.sendPost(cpUrl + "/v1/scenarios/vm",
                    JsonUtils.toUnderlineJSONString(addVmRequest));
            JSONObject addVmObj = JSONObject.parseObject(addVmRsp);
            log.info("add trex vm response:{}", addVmObj.toJSONString());

            if (BaseConstants.SUCCESS_ZERO.equals(addVmObj.getString(BaseConstants.STR_CODE))) {
                JSONObject vmObj = addVmObj.getJSONObject(BaseConstants.STR_DATA);
                String instanceId = vmObj.getString("id");
                JSONObject netWorkObj = vmObj.getJSONArray("networks").getJSONObject(0);
                JSONObject portMappingObj = netWorkObj.getJSONArray("access_modes").getJSONObject(0).getJSONObject(
                        "port_mapping");
                String host = portMappingObj.getString("host");
                String port = portMappingObj.getString("host_port");
                String ip = host + BaseConstants.COLON + port;

                // 给trex虚拟机添加server网口
                JSONObject addPortReq = new JSONObject();
                addPortReq.put("network_id", request.getNetworkId());
                String addTrexServerPortRsp = HttpUtils.sendPost(cpUrl + "/v1/instances/" + instanceId + "/interfaces"
                        , addPortReq.toJSONString());
                JSONObject addTrexServerObj = JSONObject.parseObject(addTrexServerPortRsp);
                log.info("add trex server port response:{}", addTrexServerObj.toJSONString());
                String serverMac = "";
                String serverIp = "";
                if (BaseConstants.SUCCESS_ZERO.equals(addTrexServerObj.getString(BaseConstants.STR_CODE))) {
                    JSONObject serverDataObj = addTrexServerObj.getJSONObject(BaseConstants.STR_DATA);
                    serverMac = serverDataObj.getString("mac_address");
                    serverIp = serverDataObj.getString("fixed_ip");
                } else {
                    log.error("add trex server port error");
                }

                // 给trex虚拟机添加client网口
                String addTrexClientPortRsp = HttpUtils.sendPost(cpUrl + "/v1/instances/" + instanceId + "/interfaces"
                        , addPortReq.toJSONString());
                JSONObject addTrexClientObj = JSONObject.parseObject(addTrexClientPortRsp);
                log.info("add trex client port response:{}", addTrexClientObj.toJSONString());
                String clientMac = "";
                String clientIp = "";
                if (BaseConstants.SUCCESS_ZERO.equals(addTrexClientObj.getString(BaseConstants.STR_CODE))) {
                    JSONObject clientDataObj = addTrexClientObj.getJSONObject(BaseConstants.STR_DATA);
                    clientMac = clientDataObj.getString("mac_address");
                    clientIp = clientDataObj.getString("fixed_ip");
                } else {
                    log.error("add trex client port error");
                }

                // 查询默认网关
                String getGateWayRsp = HttpUtils.sendGet(cpUrl + "/v1/networks/" + request.getNetworkId());
                JSONObject getGateWayObj = JSONObject.parseObject(getGateWayRsp);
                log.info("get gateway response:{}", getGateWayObj.toJSONString());
                String gatewayIp = "";
                if (BaseConstants.SUCCESS_ZERO.equals(getGateWayObj.getString(BaseConstants.STR_CODE))) {
                    gatewayIp =
                            getGateWayObj.getJSONObject(BaseConstants.STR_DATA).getJSONObject("subnet").getString(
                                    "gateway_ip");
                } else {
                    log.error("get gateway info error");
                }

                // 新增流量执行器信息
                flowActuatorModel.setId(uuid);
                flowActuatorModel.setTitle(addVmRequest.getName());
                flowActuatorModel.setNetworkId(request.getNetworkId());
                flowActuatorModel.setInstanceId(instanceId);
                flowActuatorModel.setIp(ip);
                flowActuatorModel.setServerMac(serverMac);
                flowActuatorModel.setServerIp(serverIp);
                flowActuatorModel.setServerGateway(gatewayIp);
                flowActuatorModel.setClientMac(clientMac);
                flowActuatorModel.setClientIp(clientIp);
                flowActuatorModel.setClientGateway(gatewayIp);
                flowActuatorMapper.create(flowActuatorModel);
            } else {
                log.error("add trex vm error");
            }
        } catch (Exception e) {
            log.error("add vm instance error", e);
        }

        log.info("================ Add vm instance end ==================");
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

}
