package cn.cyberpeace.mp.flow.integration.cloud.topocloud;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.controller.FlowActuatorController;
import cn.cyberpeace.mp.flow.integration.cloud.base.AddVirtualNicParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.AddVirtualNicResult;
import cn.cyberpeace.mp.flow.integration.cloud.base.DeleteVirtualMachineResult;
import cn.cyberpeace.mp.flow.integration.cloud.base.GetVirtualMachineResult;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import cn.cyberpeace.mp.flow.integration.cloud.base.VirtualNic;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCSceneInstance;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCscene;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCsceneBO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.io.CharStreams;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TopoCloudClient implements ICloudClient {

    private static final String CREATE_TOPO_URL = "/api/mp/cscene/cscenes/app_instance";

    private static final String DELETE_TOPO_URL = "/api/mp/cscene/cscene_instances";

    private static final String GET_TOPO_URL = "/api/mp/cscene/cscene_instances?id={id}";

    private static final String TOPO_NAME = "trex_topo";

    private static final String TREX_NODE_ID = "6b073509-3aa4-420f-9b27-87acc6b3fad9";

    private static final String TREX_NODE_PORT_ID = "port_6b073509-3aa4-420f-9b27-87acc6b3fad9_1";

    private static final String NET_NODE_ID = "a0018ce7-9646-4386-bd9b-52518670e469";

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

    private String ip;

    private Integer port;

    private String host;

    public TopoCloudClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
        this.host = "http://" + ip + ":" + port;
    }

    @Override
    public GetVirtualMachineResult getVirtualMachine(String csceneId) {
        String url = GET_TOPO_URL.replace("{id}", csceneId);
        String res = null;
        try {
            res = HttpUtils.sendHttpGet(this.host+url, HEADER, 10000);
            TopoResponse<BusinessCSceneInstance> tmpRes = JsonCommon.snakeParse(res, new TypeReference<TopoResponse<BusinessCSceneInstance>>() {
            });
            var instance = tmpRes.getData();
            String flowNodeResourceId = this.getFlowNodeResourceId(instance);
            String ip = this.getFlowNodeIp(instance);
            String netNodeResourceId = this.getFlowNodeNetResourceId(instance);
            if (StringUtils.isNotEmpty(flowNodeResourceId)) {
                GetVirtualMachineResult result = new GetVirtualMachineResult();
                result.setId(flowNodeResourceId);
                List<VirtualNic> virtualNics = new ArrayList<>();
                result.setNetwork(virtualNics);
                VirtualNic virtualNic = new VirtualNic();
                virtualNic.setFixedIp(ip);
                virtualNic.setNetworkId(netNodeResourceId);
                result.getNetwork().add(virtualNic);
                return result;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static BusinessCSceneInstance getCscene(String csceneId) {
        String url = GET_TOPO_URL.replace("{id}", csceneId);
        String res = null;
        FlowConfig flowConfig = BeanCommon.getBean(FlowConfig.class);
        try {
            res = HttpUtils.sendHttpGet(flowConfig.getCsceneUrl() + url, HEADER, 300000);
            TopoResponse<BusinessCSceneInstance> tmpRes = JsonCommon.snakeParse(res, new TypeReference<TopoResponse<BusinessCSceneInstance>>() {
            });
            var instance = tmpRes.getData();
            return instance;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetVirtualMachineResult createVirtualMachine() {
        var businessCscene = getDefaultFlowTopoContent();
        BusinessCsceneBO businessCsceneBO = new BusinessCsceneBO();
        businessCsceneBO.setCscene(businessCscene);
//        businessCsceneBO.setId(TextUtils.uuid());
        businessCsceneBO.setName(TOPO_NAME);
        businessCsceneBO.getCscene().getSceneConfig().getTopology().getScene().setName(TOPO_NAME);
        String createRes = null;
        try {
            createRes = HttpUtils.sendHttpPost(CREATE_TOPO_URL, JsonCommon.snakeStringify(businessCsceneBO), HEADER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FlowActuatorController.TopoResponse<BusinessCSceneInstance> response = JsonCommon.snakeParse(createRes, new TypeReference<FlowActuatorController.TopoResponse<BusinessCSceneInstance>>() {
        });
        String csceneId = response.getData().getId();
        String url = GET_TOPO_URL.replace("{id}", csceneId);
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(3000);
                TopoGetVirtualMachineParams getParams = new TopoGetVirtualMachineParams();
                getParams.setId(csceneId);
                var res = this.getVirtualMachine(csceneId);
                if (res != null) {
                    return res;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public DeleteVirtualMachineResult deleteVirtualMachine(String id) {
        Map<String, String> data = Map.of("id", id);
        try {
            HttpUtils.sendHttpDelete(DELETE_TOPO_URL, JsonCommon.snakeStringify(data), HEADER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new DeleteVirtualMachineResult();
    }

    @Override
    public AddVirtualNicResult addVirtualNic(AddVirtualNicParams params) {
        return null;
    }

    private String getFlowNodeResourceId(BusinessCSceneInstance instance) {
        return instance.getScene().getPlugins().getCpcloud().getElements().get(TREX_NODE_ID).getResourceId();
    }

    private String getFlowNodeNetResourceId(BusinessCSceneInstance instance) {
        return instance.getScene().getPlugins().getCpcloud().getElements().get(NET_NODE_ID).getResourceId();
    }

    private String getFlowNodeIp(BusinessCSceneInstance instance) {
        for (var e : instance.getScene().getTopology().getElements()) {
            if (StringUtils.equals(e.getId(), TREX_NODE_ID)) {
                return e.getPorts().get(0).getIp();
            }
        }
        return "";
    }

    private BusinessCscene getDefaultFlowTopoContent() {
        BusinessCscene businessCscene = JsonCommon.snakeParse(JsonCommon.snakeStringify(BUSINESS_CSCENE), BusinessCscene.class);
        return businessCscene;
    }

    @Getter
    @Setter
    private static class TopoResponse<T> {
        private T data;
    }
}
