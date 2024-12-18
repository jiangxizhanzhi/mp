package cn.cyberpeace.mp.flow.integration;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.response.BackgroundFlowResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FlowClient {
    private static String BACKGROUND_FLOW_INFO_URL = "/api/mp/flow/info/background";

    private static String ATTACK_FLOW_INFO_URL = "";

    private static Map<String, String> HEADER = new HashMap<>();

    static {
        HEADER.put("clientId", "84261d495c414e");
        HEADER.put("clientSecret", "2112f4b855264ad7b1a4209737fc7836");
    }

    public static BackgroundFlowResponse getBackgroundFlowInfo(String id) {
        try {
            FlowConfig flowConfig = BeanCommon.getBean(FlowConfig.class);
            var res = HttpUtils.sendHttpGet(flowConfig.getCsceneUrl() + BACKGROUND_FLOW_INFO_URL + "?id=" + id, HEADER, 10000);
            ServiceResult<BackgroundFlowResponse> result = JsonCommon.snakeParse(res, new TypeReference<ServiceResult<BackgroundFlowResponse>>() {
            });
            var response = result.getData();
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getAttackFlowInfo() {

    }
}
