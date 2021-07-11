package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.config.FlowConfig;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FlowPortServiceImpl extends BaseModelServiceImpl<FlowPortModel, FlowPortMapper, FlowPortFields, QueryParams> implements FlowPortService {

    @Autowired
    private FlowConfig flowConfig;

    @Value("${mp.devopscluster.cp-ip:}")
    private String cpIp;

    public FlowPortServiceImpl(FlowPortMapper stuMapper, FlowPortFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    
    @Async
    public void batchCreatePorts(List<FlowNetworkInfo> networkInfos, FlowActuatorModel flowActuatorModel, String externalNetId) {
        if (CollectionUtils.isEmpty(networkInfos)) {
            log.error("【{}】batch create ports error for networkInfos is empty", flowActuatorModel.getTitle());
            return;
        }

        List<FlowNetworkInfo> createNetworkinfos = networkInfos.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(
                        Comparator.comparing(FlowNetworkInfo::getNetworkId))), ArrayList::new));



        List<FlowPortModel> flowPortModels = new ArrayList<>();
        createNetworkinfos.forEach(networkInfo -> {
            String networkId = networkInfo.getNetworkId();
            if (!externalNetId.equals(networkId)) {
                try {
                    JSONObject addPortReq = new JSONObject();
                    addPortReq.put("network_id", networkId);

                    String serverMac = "";
                    String serverIp = "";
                    String serverPortId = "";

                    String clientMac = "";
                    String clientIp = "";
                    String clientPortId = "";

                    
                    String addTrexClientPortRsp = HttpUtils.sendPost(flowConfig.getCpUrl() + "/v1/instances/" +
                            flowActuatorModel.getInstanceId() + "/interfaces", addPortReq.toJSONString());
                    JSONObject addTrexClientObj = JSONObject.parseObject(addTrexClientPortRsp);
                    log.info("add client port response:{}", addTrexClientObj.toJSONString());
                    if (BaseConstants.SUCCESS_ZERO.equals(addTrexClientObj.getString(BaseConstants.STR_CODE))) {
                        JSONObject clientDataObj = addTrexClientObj.getJSONObject(BaseConstants.STR_DATA);
                        clientMac = clientDataObj.getString("mac_address");
                        clientIp = clientDataObj.getString("fixed_ip");
                        clientPortId = clientDataObj.getString("port_id");

                        
                        serverMac = clientMac;
                        serverIp = clientIp;
                        serverPortId = clientPortId;
                        networkInfo.setMac(clientMac);
                    } else {
                        log.error("【{}】add client port error for networkId: {}", flowActuatorModel.getTitle(), networkId);
                        throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
                    }

                    FlowPortModel flowPortModel = FlowPortModel.builder()
                            .serverIp(serverIp)
                            .serverMac(serverMac)
                            .serverPortId(serverPortId)
                            .serverGateway(networkInfo.getIp()) 
                            .clientIp(clientIp)
                            .clientMac(clientMac)
                            .clientPortId(clientPortId)
                            .clientGateway(networkInfo.getIp()) 
                            .networkId(networkId)
                            .actuatorId(flowActuatorModel.getId())
                            .build();

                    flowPortModels.add(flowPortModel);
                } catch (IOException e) {
                    log.error("【{}】add flow port error for networkId: {}", flowActuatorModel.getTitle(), networkId, e);
                    throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
                }
            }
        });

        
        getModelMapper().bulkCreate(flowPortModels);
        String outIp = cpIp.substring(0, cpIp.lastIndexOf(".")).concat(".254");
        
        networkInfos.forEach(networkInfo -> {
            createNetworkinfos.forEach(createNetworkinfo -> {
                if (networkInfo.getNetworkId().equals(createNetworkinfo.getNetworkId())) {
                    networkInfo.setMac(createNetworkinfo.getMac());
                }
            });

            if (StringUtils.isBlank(networkInfo.getIp())) {
                networkInfo.setIp(outIp);
            }
        });
        JSONObject taskInitObj = new JSONObject();
        taskInitObj.put("network_infos", networkInfos);
        String taskInitRsp;
        try {
            taskInitRsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/init",
                    taskInitObj.toJSONString());
        } catch (IOException e) {
            log.error("tasks init error, {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
        }
        JSONObject taskResultObj = JSONObject.parseObject(taskInitRsp);
        log.info("tasks init response:{}", taskResultObj.toJSONString());
        if ("1000".equalsIgnoreCase(taskResultObj.getString("code"))) {
            log.info("tasks init response success");
        } else {
            log.error("tasks init response error");
        }
    }

}
