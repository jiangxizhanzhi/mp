package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.flow.enums.FlowPlatformEnum;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowPortModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowPortMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.AddAppTaskRequest;
import cn.cyberpeace.mp.flow.request.AddFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.AddStandardsTaskRequest;
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
import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    /**
     * 异步保存流量发生平台任务
     *
     * @param request
     * @param isUpdate
     */
    @Async
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
                log.error("get flow port info error");
                return;
            }
        } else {
            // 新增时，创建网口信息
            flowPortModel = this.createPort(flowActuatorModel, flowInfoModel.getResourceType());
            if (Objects.isNull(flowPortModel)) {
                log.error("create flow port info error");
                return;
            }
        }

        flowTaskModel.setFlowPortId(flowPortModel.getId());

        String thirdTaskId = "";
        if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
            // 构建保存app任务请求
            AddAppTaskRequest appTaskRequest = new AddAppTaskRequest(flowTaskModel, flowPortModel);

            try {
                String saveAppTaskRsp = null;
                if (isUpdate) {
                    // 更新
                    saveAppTaskRsp = HttpUtils.sendPatch(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/app/"
                                    + flowTaskModel.getThirdTaskId(), JsonUtils.toUnderlineJSONString(appTaskRequest));
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
                standTaskRequest.setPort(flowPortModel.getServerMac());
                String saveStandTaskRsp = null;
                if (isUpdate) {
                    saveStandTaskRsp = HttpUtils.sendPatch(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/standards/" +
                                    flowTaskModel.getThirdTaskId(), JsonUtils.toUnderlineJSONString(standTaskRequest));
                } else {
                    saveStandTaskRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/standards",
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
     * @return
     */
    public String addExecutor() {
        String uuid = TextUtils.uuid();
        this.addFlowExecutor(uuid);
        return uuid;
    }

    /**
     * 异步新增虚拟机、添加网口
     *
     */
    @Async
    public void addExecutor(String uuid) {
        this.addFlowExecutor(uuid);
    }

    /**
     * 新增流量执行器
     * @param uuid
     */
    private void addFlowExecutor(String uuid) {
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
            return;
        }

        FlowActuatorModel flowActuatorModel = new FlowActuatorModel();
        AddVmRequest addVmRequest = new AddVmRequest();
        addVmRequest.setName(FlowPlatformEnum.TREX.toString() + System.currentTimeMillis());
        addVmRequest.setImage(trexImage);
        AddVmRequest.Networks networks = new AddVmRequest.Networks();
        networks.setNetworkId(externalNetId);
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

            String fixedIp = null;
            if (BaseConstants.SUCCESS_ZERO.equals(addVmObj.getString(BaseConstants.STR_CODE))) {
                JSONObject vmObj = addVmObj.getJSONObject(BaseConstants.STR_DATA);
                String instanceId = vmObj.getString("id");
                JSONObject netWorkObj = vmObj.getJSONArray("networks").getJSONObject(0);
                fixedIp = netWorkObj.getString("fixed_ip");
                JSONObject portMappingObj = netWorkObj.getJSONArray("access_modes").getJSONObject(0).getJSONObject(
                        "port_mapping");
                String host = portMappingObj.getString("host");
                String port = portMappingObj.getString("host_port");
                String ip = host + BaseConstants.COLON + port;

                // 新增流量执行器信息
                flowActuatorModel.setId(uuid);
                flowActuatorModel.setTitle(addVmRequest.getName());
                flowActuatorModel.setInstanceId(instanceId);
                flowActuatorModel.setIp(ip);
                flowActuatorMapper.create(flowActuatorModel);

                // 轮询流量发生器端口，直到端口可访问才返回
                long startTime = System.currentTimeMillis();
                boolean isConnectable = isHostConnectable(fixedIp);
                while (!isConnectable) {
                    isConnectable = isHostConnectable(fixedIp);
                    if (isConnectable) {
                        log.info("trex host[{}] connect success", fixedIp);
                    } else {
                        long endTime = System.currentTimeMillis();
                        if ((endTime - startTime) > (1000 * 60 * 10)) {
                            // 十分钟服务没起来，结束轮询
                            log.error("trex vm start error:{}", addVmRequest.getName());
                            isConnectable = true;
                        }
                    }
                }
            } else {
                log.error("add trex vm error");
            }
        } catch (Exception e) {
            log.error("add vm instance error", e);
        }

        log.info("================ Add vm instance end ==================");
    }

    /**
     * 判断端口是否能连接
     * @param host
     * @return
     */
    public static boolean isHostConnectable(String host) {
        TelnetClient client = new TelnetClient();
        // 超时时间设为2s
        client.setConnectTimeout(1000);
        try {
            client.connect(host, 5001);
        } catch (IOException e) {
            log.warn("trex host[{}] connect error", host);
            return false;
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                log.error("client disconnect error", e);
            }
        }

        return true;
    }

    /**
     * 新增网口
     *
     * @param flowActuatorModel
     * @return
     */
    private FlowPortModel createPort(FlowActuatorModel flowActuatorModel, int resourceType) {
        FlowPortModel flowPortModel = null;
        try {
            // 给trex虚拟机添加server网口
            JSONObject addPortReq = new JSONObject();
            addPortReq.put("network_id", flowActuatorModel.getNetworkId());
            String addTrexServerPortRsp = HttpUtils.sendPost(cpUrl + "/v1/instances/" +
                            flowActuatorModel.getInstanceId() + "/interfaces", addPortReq.toJSONString());
            JSONObject addTrexServerObj = JSONObject.parseObject(addTrexServerPortRsp);
            log.info("add trex server port response:{}", addTrexServerObj.toJSONString());
            String serverMac = "";
            String serverIp = "";
            String serverPortId = "";
            if (BaseConstants.SUCCESS_ZERO.equals(addTrexServerObj.getString(BaseConstants.STR_CODE))) {
                JSONObject serverDataObj = addTrexServerObj.getJSONObject(BaseConstants.STR_DATA);
                serverMac = serverDataObj.getString("mac_address");
                serverIp = serverDataObj.getString("fixed_ip");
                serverPortId = serverDataObj.getString("port_id");
            } else {
                log.error("add trex server port error");
            }

            String clientMac = "";
            String clientIp = "";
            String clientPortId = "";
            if (ResourceTypeEnum.PACP.toValue() == resourceType) {
                // pcap需要给trex虚拟机添加client网口
                String addTrexClientPortRsp =
                        HttpUtils.sendPost(cpUrl + "/v1/instances/" + flowActuatorModel.getInstanceId() + "/interfaces"
                                , addPortReq.toJSONString());
                JSONObject addTrexClientObj = JSONObject.parseObject(addTrexClientPortRsp);
                log.info("add trex client port response:{}", addTrexClientObj.toJSONString());
                if (BaseConstants.SUCCESS_ZERO.equals(addTrexClientObj.getString(BaseConstants.STR_CODE))) {
                    JSONObject clientDataObj = addTrexClientObj.getJSONObject(BaseConstants.STR_DATA);
                    clientMac = clientDataObj.getString("mac_address");
                    clientIp = clientDataObj.getString("fixed_ip");
                    clientPortId = clientDataObj.getString("port_id");
                } else {
                    log.error("add trex client port error");
                }
            }


            // 查询默认网关
            String getGateWayRsp = HttpUtils.sendGet(cpUrl + "/v1/networks/" + flowActuatorModel.getNetworkId());
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

            flowPortModel = FlowPortModel.builder()
                    .serverIp(serverIp)
                    .serverMac(serverMac)
                    .serverPortId(serverPortId)
                    .serverGateway(gatewayIp)
                    .clientIp(clientIp)
                    .clientMac(clientMac)
                    .clientPortId(clientPortId)
                    .clientGateway(gatewayIp)
                    .build();
            flowPortMapper.create(flowPortModel);
        } catch (Exception e) {
            log.error("add flow port error", e);
        }

        return flowPortModel;
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

}
