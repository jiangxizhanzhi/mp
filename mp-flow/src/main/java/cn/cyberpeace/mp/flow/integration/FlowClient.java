package cn.cyberpeace.mp.flow.integration;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.response.BackgroundFlowResponse;
import cn.cyberpeace.mp.flow.response.RemoteFlowInfo;
import cn.cyberpeace.mp.flow.response.ScriptParamModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
public class FlowClient {
    private static String BACKGROUND_FLOW_INFO_URL = BaseAppConfig.API_PREFIX + "/flow/info/background";

    private static String ATTACK_FLOW_INFO_URL = BaseAppConfig.API_PREFIX + "/flow/info/attack";

    private static Map<String, String> HEADER = new HashMap<>();

    static {
        HEADER.put("clientId", "84261d495c414e");
        HEADER.put("clientSecret", "2112f4b855264ad7b1a4209737fc7836");
    }

    public static List<RemoteFlowInfo> getRemoteFlowInfos(Set<String> flowIds) {
        List<RemoteFlowInfo> flowInfos = new ArrayList<>();
        for (var flowId : flowIds) {
            RemoteFlowInfo flowInfo = getRemoteFlowInfoNoException(flowId, 2);
            if (flowInfo != null) {
                flowInfos.add(flowInfo);
            } else {
                log.info("未查询到流量,flow id : {}", flowId);
            }
        }
        return flowInfos;
    }

    private static RemoteFlowInfo getRemoteFlowInfoNoException(String flowId, int tryCount) {
        RemoteFlowInfo flow = null;
        for (int i = 0; i < tryCount; i++) {
            try {
                flow = FlowClient.getRemoteFlowInfo(flowId);
                if (flow != null) {
                    break;
                }
            } catch (Exception e) {
                log.error("failed to query flow , flow id is {}", flowId, e);
            }
        }
        return flow;
    }

    public static RemoteFlowInfo getRemoteFlowInfo(String id) {
        RemoteFlowInfoResponse response = getRemoteBackgroundFlowInfo(id);
        if (response == null) {
            response = getRemoteAttackFlowInfo(id);
        }
        if (response == null) {
            return null;
        }
        RemoteFlowInfo remoteFlowInfo = new RemoteFlowInfo();
        remoteFlowInfo.setTitle(response.getTitle());
        remoteFlowInfo.setFlowId(response.getResourceId());
        remoteFlowInfo.setInteractiveType(response.getInteractiveType());
        remoteFlowInfo.setFile(response.getFile());
        remoteFlowInfo.setFileName(response.getFileName());
        remoteFlowInfo.setPcapFile(response.getPcapFile());
        remoteFlowInfo.setProtocolList(response.getProtocolList());
        remoteFlowInfo.setPcapSize(response.getPcapSize());
        remoteFlowInfo.setDuration(response.getDuration());
        remoteFlowInfo.setPktNum(response.getPktNum());
        remoteFlowInfo.setCategory(response.getCategory());
        remoteFlowInfo.setResourceType(response.getResourceType());
        remoteFlowInfo.setFlowDesc(response.getFlowDesc());
        if (StringUtils.isNotEmpty(response.getParameter())) {
            var params = JsonCommon.parse(response.getParameter(), new TypeReference<Map<String, List<String>>>() {
            });
            List<ScriptParamModel> paramModels = new ArrayList<>();
            for (var entry : params.entrySet()) {
                if (entry.getValue().size() != 3) {
                    log.info("脚本流量参数格式错误 ，key: {} , value: {}", entry.getKey(), JsonCommon.stringify(entry.getValue()));
                    continue;
                }
                ScriptParamModel scriptParamModel = new ScriptParamModel();
                scriptParamModel.setParamKey(entry.getKey());
                scriptParamModel.setParamType(entry.getValue().get(0));
                scriptParamModel.setParamValue(entry.getValue().get(1));
                scriptParamModel.setParamDesc(entry.getValue().get(2));
                paramModels.add(scriptParamModel);
            }
        }
        return remoteFlowInfo;
    }

    public static BackgroundFlowResponse getBackgroundFlowInfo(String id) {
        try {
            FlowConfig flowConfig = BeanCommon.getBean(FlowConfig.class);
            var res = HttpUtils.sendHttpGet(flowConfig.getCsceneUrl() + BACKGROUND_FLOW_INFO_URL + "?id=" + id, HEADER, 30000);
            ServiceResult<BackgroundFlowResponse> result = JsonCommon.snakeParse(res, new TypeReference<ServiceResult<BackgroundFlowResponse>>() {
            });
            var response = result.getData();
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static RemoteFlowInfoResponse getRemoteBackgroundFlowInfo(String id) {
        try {
            FlowConfig flowConfig = BeanCommon.getBean(FlowConfig.class);
            var res = HttpUtils.sendHttpGet(flowConfig.getCsceneUrl() + BACKGROUND_FLOW_INFO_URL + "?id=" + id, HEADER, 30000);
            ServiceResult<RemoteFlowInfoResponse> result = JsonCommon.snakeParse(res, new TypeReference<ServiceResult<RemoteFlowInfoResponse>>() {
            });
            var response = result.getData();
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static RemoteFlowInfoResponse getRemoteAttackFlowInfo(String id) {
        try {
            FlowConfig flowConfig = BeanCommon.getBean(FlowConfig.class);
            var res = HttpUtils.sendHttpGet(flowConfig.getCsceneUrl() + ATTACK_FLOW_INFO_URL + "?id=" + id, HEADER, 30000);
            ServiceResult<RemoteFlowInfoResponse> result = JsonCommon.snakeParse(res, new TypeReference<ServiceResult<RemoteFlowInfoResponse>>() {
            });
            var response = result.getData();
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Getter
    @Setter
    private static class RemoteFlowInfoResponse {
        private String resourceId;
        private String title;
        private String file;
        private String fileName;
        private String pcapFile;
        private Long duration;
        private Long pcapSize;
        private Long pktNum;
        private List<ProtocolBO> protocolList;
        private String parameter;
        private Integer interactiveType;
        private Integer category;
        private Integer resourceType;
        private String flowDesc;
    }
}
