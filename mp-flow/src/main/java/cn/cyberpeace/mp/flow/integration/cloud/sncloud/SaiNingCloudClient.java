package cn.cyberpeace.mp.flow.integration.cloud.sncloud;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.integration.cloud.base.*;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
//@Service("saiNingCloudClient")
public class SaiNingCloudClient implements ICloudClient {

    private static final String VIRTUAL_MACHINE_GET_URL = "/v1/instances/{instance_id}";

    private static final String ADD_VIRTUAL_NIC_URL = "/v1/instances/{instanceId}/interfaces";

    private static final String VIRTUAL_MACHINE_CREATE_URL = "/v1/scenarios/vm";

    private static final String VIRTUAL_MACHINE_DELETE_URL = "/v1/scenarios/vm";

    private String cloudHost;

    public SaiNingCloudClient(@Autowired FlowConfig flowConfig) {
        this.cloudHost = flowConfig.getCpUrl();
    }

    @Override
    public GetVirtualMachineResult getVirtualMachine(String id) {
        SnGetVirtualMachineParams snParams = new SnGetVirtualMachineParams();
        snParams.setId(id);
        try {
            String url = this.cloudHost + VIRTUAL_MACHINE_GET_URL;
            url = url.replace("{instance_id}", snParams.getId());
            var resp = HttpUtils.sendGet(url);
            var rsp = JsonCommon.snakeParse(resp, new TypeReference<CloudResponse<GetVirtualMachineResult>>() {
            });
            if ("0".equals(rsp.getCode())) {
                return rsp.getData();
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetVirtualMachineResult createVirtualMachine() {
        SnCreateVirtualMachineParams snParams = SnCreateVirtualMachineParams.getDefault();
        try {
            String url = this.cloudHost + VIRTUAL_MACHINE_CREATE_URL;
            var resp = HttpUtils.sendPost(url, JsonCommon.snakeStringify(snParams));
            var rsp = JsonCommon.snakeParse(resp, new TypeReference<CloudResponse<GetVirtualMachineResult>>() {
            });
            if ("0".equals(rsp.getCode())) {
                return rsp.getData();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DeleteVirtualMachineResult deleteVirtualMachine(String id) {
        SnDeleteVirtualMachineParams snParams = new SnDeleteVirtualMachineParams();
        snParams.setVms(Arrays.asList(id));
        try {
            String url = this.cloudHost + VIRTUAL_MACHINE_DELETE_URL;
            var resp = HttpUtils.sendDelete(url, JsonCommon.snakeStringify(snParams));
            var rsp = JsonCommon.snakeParse(resp, new TypeReference<CloudResponse<DeleteVirtualMachineResult>>() {
            });
            if ("0".equals(rsp.getCode())) {
                return rsp.getData();
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AddVirtualNicResult addVirtualNic(AddVirtualNicParams params) {
        Map<String, String> body = Map.of("network_id", params.getNetworkId());
        try {
            String url = ADD_VIRTUAL_NIC_URL.replace("{instanceId}", params.getInstanceId());
            url = this.cloudHost + url;
            var addTrexClientPortRsp = HttpUtils.sendHttpPost(url, JsonCommon.snakeStringify(body), new HashMap<>());
            CloudResponse<AddVirtualNicResponse> response = JsonCommon.snakeParse(addTrexClientPortRsp, new TypeReference<CloudResponse<AddVirtualNicResponse>>() {
            });
            if (!"0".equals(response.getCode()) || response.getData() == null) {
                log.error("failed to add nic , instacne {} , network {} , result {}", params.getInstanceId(), params.getNetworkId(), addTrexClientPortRsp);
                throw new RuntimeException("add nic error");
            }
            AddVirtualNicResult result = new AddVirtualNicResult();
            result.setMacAddress(response.getData().getMacAddress());
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @Setter
    private static class CloudResponse<T> {
        private String code;

        private T data;
    }

    @Getter
    @Setter
    private static class AddVirtualNicResponse {
        private String macAddress;
    }

}
