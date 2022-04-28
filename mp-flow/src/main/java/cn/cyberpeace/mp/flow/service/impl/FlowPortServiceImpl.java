package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import cn.cyberpeace.mp.flow.model.FlowPortModel;
import cn.cyberpeace.mp.flow.repository.FlowPortMapper;
import cn.cyberpeace.mp.flow.service.FlowPortService;
import cn.cyberpeace.mp.flow.service.field.FlowPortFields;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 流量网口service
 * @Author qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowPortServiceImpl extends BaseModelServiceImpl<FlowPortModel, FlowPortMapper, FlowPortFields, QueryParams> implements FlowPortService {

    /**
     * 云平台地址
     */
    @Value("${mp.devops.cp-url}")
    private String cpUrl;


    public FlowPortServiceImpl(FlowPortMapper stuMapper, FlowPortFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 异步批量新增网口
     * @param networkInfos
     * @param flowActuatorModel
     */
    @Async
    public void batchCreatePorts(List<FlowNetworkInfo> networkInfos, FlowActuatorModel flowActuatorModel) {
        log.info("=========== batch create ports start =============");
        List<String> networkIdList = new ArrayList<>();
        if (CollectionUtils.isEmpty(networkIdList)) {
            log.error("【{}】batch create ports error for networkIdList is empty", flowActuatorModel.getTitle());
            return;
        }

        List<FlowPortModel> flowPortModels = new ArrayList<>();
        List<String> macList = new ArrayList<>();
        networkIdList.forEach(networkId -> {
            try {
                JSONObject addPortReq = new JSONObject();
                addPortReq.put("network_id", networkId);

                String serverMac = "";
                String serverIp = "";
                String serverPortId = "";

                String clientMac = "";
                String clientIp = "";
                String clientPortId = "";

                // pcap需要给trex虚拟机添加client网口
                String addTrexClientPortRsp = HttpUtils.sendPost(cpUrl + "/v1/instances/" +
                                flowActuatorModel.getInstanceId() + "/interfaces", addPortReq.toJSONString());
                JSONObject addTrexClientObj = JSONObject.parseObject(addTrexClientPortRsp);
                log.info("add client port response:{}", addTrexClientObj.toJSONString());
                if (BaseConstants.SUCCESS_ZERO.equals(addTrexClientObj.getString(BaseConstants.STR_CODE))) {
                    JSONObject clientDataObj = addTrexClientObj.getJSONObject(BaseConstants.STR_DATA);
                    clientMac = clientDataObj.getString("mac_address");
                    clientIp = clientDataObj.getString("fixed_ip");
                    clientPortId = clientDataObj.getString("port_id");

                    // 客户端和服务端共用一个网口
                    serverMac = clientMac;
                    serverIp = clientIp;
                    serverPortId = clientPortId;

                    macList.add(clientMac);
                } else {
                    log.error("【{}】add client port error for networkId: {}", flowActuatorModel.getTitle(), networkId);
                    throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
                }

                // 查询默认网关
                String getGateWayRsp = HttpUtils.sendGet(cpUrl + "/v1/networks/" + networkId);
                JSONObject getGateWayObj = JSONObject.parseObject(getGateWayRsp);
                log.info("get gateway response for:{}", getGateWayObj.toJSONString());
                String gatewayIp = "";
                if (BaseConstants.SUCCESS_ZERO.equals(getGateWayObj.getString(BaseConstants.STR_CODE))) {
                    gatewayIp =
                            getGateWayObj.getJSONObject(BaseConstants.STR_DATA).getJSONObject("subnet").getString(
                                    "gateway_ip");
                } else {
                    log.error("【{}】get gateway info error for networkId:{}", flowActuatorModel.getTitle(), networkId);
                    throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
                }

                FlowPortModel flowPortModel = FlowPortModel.builder()
                        .serverIp(serverIp)
                        .serverMac(serverMac)
                        .serverPortId(serverPortId)
                        .serverGateway(gatewayIp)
                        .clientIp(clientIp)
                        .clientMac(clientMac)
                        .clientPortId(clientPortId)
                        .clientGateway(gatewayIp)
                        .networkId(networkId)
                        .actuatorId(flowActuatorModel.getId())
                        .build();

                flowPortModels.add(flowPortModel);
            } catch (IOException e) {
                log.error("【{}】add flow port error for networkId: {}", flowActuatorModel.getTitle(), networkId, e);
                throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
            }

            // 批量新增网口信息
            getModelMapper().bulkCreate(flowPortModels);
        });

        // 流量发生器初始化网口信息
        if (CollectionUtils.isNotEmpty(macList)) {
            JSONObject taskInitObj = new JSONObject();
            taskInitObj.put("macs", macList);
            String taskInitRsp = null;
            try {
                taskInitRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/init",
                        taskInitObj.toJSONString());
            } catch (IOException e) {
                log.error("tasks init error", e);
                throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
            }
            JSONObject taskResultObj = JSONObject.parseObject(taskInitRsp);
            log.info("tasks init response:{}", taskResultObj.toJSONString());
            if ("1000".equalsIgnoreCase(taskResultObj.getString("code"))) {

            } else {
                log.error("tasks init response error");
            }
        }

        log.info("=========== batch create ports end =============");
    }

}
