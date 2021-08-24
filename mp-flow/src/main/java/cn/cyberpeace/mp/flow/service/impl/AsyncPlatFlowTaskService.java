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

/**
 * @Description: 流量任务异步执行服务类
 * @Autho qiaokang
 * @Date：2021/8/20 09:56
 */
@Service
@Slf4j
public class AsyncPlatFlowTaskService {

    /**
     * 流量发生平台任务地址
     */
    @Value("${mp.flow.actuator-task-url}")
    private String actuatorTaskUrl;

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

    /**
     * server镜像名称
     */
    @Value("${mp.flow.server-image}")
    private String serverImage;

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
        String sceneId = flowTaskModel.getSceneId();
        if (StringUtils.isBlank(sceneId)) {
            log.error("sceneId in flow task is empty");
            return;
        }
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.selectBySceneId(sceneId);
        if (Objects.isNull(flowActuatorModel)) {
            log.error("flow actuator is empty for sceneId:{}", sceneId);
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
                    saveAppTaskRsp = HttpUtils.sendPatch(actuatorTaskUrl + "/tasks/app/" + flowTaskModel.getThirdTaskId(),
                            JsonUtils.toUnderlineJSONString(appTaskRequest));
                } else {
                    // 新增
                    saveAppTaskRsp = HttpUtils.sendPost(actuatorTaskUrl + "/tasks/app",
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
                    saveStandTaskRsp = HttpUtils.sendPatch(actuatorTaskUrl + "/tasks/standards" + flowTaskModel.getThirdTaskId(),
                            JsonUtils.toUnderlineJSONString(standTaskRequest));
                } else {
                    saveStandTaskRsp = HttpUtils.sendPost(actuatorTaskUrl + "/tasks/standards",
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
     * @param resourceType
     * @param thirdTaskIds
     */
    @Async
    public void deletePlatFlowTask(int resourceType, List<String> thirdTaskIds) {
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
            String rsp = HttpDeleteUtil.sendDelete(actuatorTaskUrl + delUrl, object.toJSONString());
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
     * @param request
     * @return
     */
    @Async
    public void addVmInstance(AddVmInstanceRequest request) {
        log.info("================ Add vm instance start ==================");
        FlowActuatorModel flowActuatorModel = new FlowActuatorModel();
        AddVmRequest addVmRequest = new AddVmRequest();
        addVmRequest.setName("trex" + System.currentTimeMillis());
        addVmRequest.setImage(trexImage);
        AddVmRequest.Networks networks = new AddVmRequest.Networks();
        networks.setNetworkId(request.getNetworkId());
        addVmRequest.setNetworks(Collections.singletonList(networks));

        try {
            // 创建trex虚拟机
            String addVmRsp = HttpUtils.sendPost(cpUrl + "/v1/scenarios/vm", JsonUtils.toUnderlineJSONString(addVmRequest));
            JSONObject addVmObj = JSONObject.parseObject(addVmRsp);
            log.info("add trex vm response:{}", addVmObj.toJSONString());

            if (BaseConstants.SUCCESS_ZERO.equals(addVmObj.getString(BaseConstants.STR_CODE))) {
                JSONObject vmObj = addVmObj.getJSONObject(BaseConstants.STR_DATA);
                String instanceId = vmObj.getString("id");
                JSONObject netWorkObj = vmObj.getJSONArray("networks").getJSONObject(0);
                String ip = netWorkObj.getString("fixed_ip");

                // 给trex虚拟机添加server网口
                JSONObject addPortReq = new JSONObject();
                addPortReq.put("network_id", request.getNetworkId());
                String addTrexServerPortRsp = HttpUtils.sendPost(cpUrl + "/v1/instances/" + instanceId + "/interfaces", addPortReq.toJSONString());
                JSONObject addTrexServerObj = JSONObject.parseObject(addTrexServerPortRsp);
                log.info("add trex server port response:{}", addTrexServerObj.toJSONString());
                String serverMac = "";
                if (BaseConstants.SUCCESS_ZERO.equals(addTrexServerObj.getString(BaseConstants.STR_CODE))) {
                    serverMac = addTrexServerObj.getJSONObject(BaseConstants.STR_DATA).getString("mac_address");
                } else {
                    log.error("add trex server port error");
                }

                // 给trex虚拟机添加client网口
                String addTrexClientPortRsp = HttpUtils.sendPost(cpUrl + "/v1/instances/" + instanceId + "/interfaces", addPortReq.toJSONString());
                JSONObject addTrexClientObj = JSONObject.parseObject(addTrexClientPortRsp);
                log.info("add trex client port response:{}", addTrexClientObj.toJSONString());
                String clientMac = "";
                if (BaseConstants.SUCCESS_ZERO.equals(addTrexClientObj.getString(BaseConstants.STR_CODE))) {
                    clientMac = addTrexClientObj.getJSONObject(BaseConstants.STR_DATA).getString("mac_address");
                } else {
                    log.error("add trex client port error");
                }

                // 查询默认网关
                String getGateWayRsp = HttpUtils.sendGet(cpUrl + "/v1/networks/" + request.getNetworkId());
                JSONObject getGateWayObj = JSONObject.parseObject(getGateWayRsp);
                log.info("get gateway response:{}", getGateWayObj.toJSONString());
                String gatewayIp = "";
                if (BaseConstants.SUCCESS_ZERO.equals(getGateWayObj.getString(BaseConstants.STR_CODE))) {
                    gatewayIp = getGateWayObj.getJSONObject(BaseConstants.STR_DATA).getJSONObject("subnet").getString("gateway_ip");
                } else {
                    log.error("get gateway info error");
                }

                // 创建server虚拟机
                addVmRequest.setName("trexServer" + System.currentTimeMillis());
                addVmRequest.setImage(serverImage);
                String addServerVmRsp = HttpUtils.sendPost(cpUrl + "/v1/scenarios/vm", JsonUtils.toUnderlineJSONString(addVmRequest));
                JSONObject addServerVmObj = JSONObject.parseObject(addServerVmRsp);
                log.info("add server vm response:{}", addServerVmObj.toJSONString());
                String serverIp = "";
                String clientIp = "";
                if (BaseConstants.SUCCESS_ZERO.equals(addServerVmObj.getString(BaseConstants.STR_CODE))) {
                    JSONObject serverVmObj = addServerVmObj.getJSONObject(BaseConstants.STR_DATA);
                    String serverInstanceId = serverVmObj.getString("id");

                    // 添加server虚拟机的server端口
                    String addServerPortRsp = HttpUtils.sendPost(cpUrl + "/v1/instances/" + serverInstanceId + "/interfaces", addPortReq.toJSONString());
                    JSONObject addServerPortObj = JSONObject.parseObject(addServerPortRsp);
                    log.info("add server port response:{}", addServerPortObj.toJSONString());
                    if (BaseConstants.SUCCESS_ZERO.equals(addServerPortObj.getString(BaseConstants.STR_CODE))) {
                        serverIp = addServerPortObj.getJSONObject(BaseConstants.STR_DATA).getString("fixed_ip");
                    } else {
                        log.error("add server port error");
                    }

                    // 添加server虚拟机的client端口
                    String addClientPortRsp = HttpUtils.sendPost(cpUrl + "/v1/instances/" + serverInstanceId + "/interfaces", addPortReq.toJSONString());
                    JSONObject addClientPortObj = JSONObject.parseObject(addClientPortRsp);
                    log.info("add client port response:{}", addClientPortObj.toJSONString());
                    if (BaseConstants.SUCCESS_ZERO.equals(addClientPortObj.getString(BaseConstants.STR_CODE))) {
                        clientIp = addClientPortObj.getJSONObject(BaseConstants.STR_DATA).getString("fixed_ip");
                    } else {
                        log.error("add client port error");
                    }
                } else {
                    log.error("add server vm error");
                }

                // 新增流量执行器信息
                flowActuatorModel.setTitle(addVmRequest.getName());
                flowActuatorModel.setSceneId(request.getSceneId());
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

}
