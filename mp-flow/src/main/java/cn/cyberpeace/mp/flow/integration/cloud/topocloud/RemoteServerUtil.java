package cn.cyberpeace.mp.flow.integration.cloud.topocloud;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.controller.FlowActuatorController;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCSceneInstance;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCscene;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCsceneBO;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class RemoteServerUtil {
    public static final String CREATE_TOPO_URL = "http://10.100.27.1:8001/api/mp/cscene/cscenes/app_instance";

    public static final String DELETE_TOPO_URL = "http://10.100.27.1:8001/api/mp/cscene/cscene_instances";


    public static final String TOPO_NAME = "trex_topo";


    private static BusinessCscene BUSINESS_CSCENE;

    private static Map<String, String> HEADER = new HashMap<>();

    static {
        HEADER.put("clientId", "84261d495c414e");
        HEADER.put("clientSecret", "2112f4b855264ad7b1a4209737fc7836");

        try {
            Resource resource = new ClassPathResource(URLDecoder.decode("flow_actuator_scene.json", "UTF-8"));
            String content = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
            BUSINESS_CSCENE = JsonCommon.snakeParse(content, BusinessCscene.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(String csceneId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", csceneId);
        try {
            HttpUtils.sendHttpDelete(DELETE_TOPO_URL, JsonCommon.snakeStringify(params), HEADER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String addPort(String instanceId, String networkId) {
        String macAddress = "";
        JSONObject addPortReq = new JSONObject();
        addPortReq.put("network_id", networkId);
        try {
            String addTrexClientPortRsp = HttpUtils.sendPost("http://10.100.27.1:11111/" + "/v1/instances/" + instanceId + "/interfaces", addPortReq.toJSONString());
            JSONObject addTrexClientObj = JSONObject.parseObject(addTrexClientPortRsp);
            if (BaseConstants.SUCCESS_ZERO.equals(addTrexClientObj.getString(BaseConstants.STR_CODE))) {
                JSONObject clientDataObj = addTrexClientObj.getJSONObject(BaseConstants.STR_DATA);
                macAddress = clientDataObj.getString("mac_address");
            }
            return macAddress;
        } catch (IOException e) {
            throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
        }
    }

    public static BusinessCSceneInstance createFlowTopo() {
        var businessCscene = getDefaultFlowTopoContent();

        BusinessCsceneBO businessCsceneBO = new BusinessCsceneBO();
        businessCsceneBO.setCscene(businessCscene);
//        businessCsceneBO.setId(TextUtils.uuid());
        businessCsceneBO.setName(TOPO_NAME);
        businessCsceneBO.getCscene().getSceneConfig().getTopology().getScene().setName(TOPO_NAME);
        String res = null;
        try {
            res = HttpUtils.sendHttpPost(CREATE_TOPO_URL, JsonCommon.snakeStringify(businessCsceneBO), HEADER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FlowActuatorController.TopoResponse<BusinessCSceneInstance> response = JsonCommon.snakeParse(res, new TypeReference<FlowActuatorController.TopoResponse<BusinessCSceneInstance>>() {
        });
        return response.getData();
    }



    private static BusinessCscene getDefaultFlowTopoContent() {
        BusinessCscene businessCscene = JsonCommon.snakeParse(JsonCommon.snakeStringify(BUSINESS_CSCENE), BusinessCscene.class);
        return businessCscene;
    }
}
