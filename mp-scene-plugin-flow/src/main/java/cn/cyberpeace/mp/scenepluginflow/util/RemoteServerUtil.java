package cn.cyberpeace.mp.scenepluginflow.util;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.scenepluginflow.controller.FlowActuatorController;
import cn.cyberpeace.mp.scenepluginflow.service.business.BusinessCSceneInstance;
import cn.cyberpeace.mp.scenepluginflow.service.business.BusinessCscene;
import cn.cyberpeace.mp.scenepluginflow.service.business.BusinessCsceneBO;
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

    public static final String GET_TOPO_URL = "http://10.100.27.1:8001/api/mp/cscene/cscene_instances?id={id}";

    public static final String TOPO_NAME = "trex_topo";

    public static final String TREX_NODE_ID = "6b073509-3aa4-420f-9b27-87acc6b3fad9";

    public static final String TREX_NODE_PORT_ID = "port_6b073509-3aa4-420f-9b27-87acc6b3fad9_1";

    public static final String NET_NODE_ID = "a0018ce7-9646-4386-bd9b-52518670e469";

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
            HttpUtils.sendDelete(DELETE_TOPO_URL, JsonCommon.snakeStringify(params));
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public static BusinessCSceneInstance getTopo(String instanceId) {
        String url = GET_TOPO_URL.replace("{id}", instanceId);
        try {
            var res = HttpUtils.sendHttpGet(url, HEADER, 30000);
            FlowActuatorController.TopoResponse<BusinessCSceneInstance> response = JsonCommon.snakeParse(res, new TypeReference<FlowActuatorController.TopoResponse<BusinessCSceneInstance>>() {
            });
            return response.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void connectFlowNode() {
        // 将trex接入目标节点的网络 并初始化网口
    }

    public static String getFlowNodeResourceId(BusinessCSceneInstance instance) {
        return instance.getScene().getPlugins().getCpcloud().getElements().get(TREX_NODE_ID).getResourceId();
    }

    public static String getFlowNodeNetResourceId(BusinessCSceneInstance instance) {
        return instance.getScene().getPlugins().getCpcloud().getElements().get(NET_NODE_ID).getResourceId();
    }

    public static String getFlowNodeIp(BusinessCSceneInstance instance) {
        for (var e : instance.getScene().getTopology().getElements()) {
            if (StringUtils.equals(e.getId(), TREX_NODE_ID)) {
                return e.getPorts().get(0).getIp();
            }
        }
        return "";
    }

    private static BusinessCscene getDefaultFlowTopoContent() {
        BusinessCscene businessCscene = JsonCommon.snakeParse(JsonCommon.snakeStringify(BUSINESS_CSCENE), BusinessCscene.class);
        return businessCscene;
    }
}
