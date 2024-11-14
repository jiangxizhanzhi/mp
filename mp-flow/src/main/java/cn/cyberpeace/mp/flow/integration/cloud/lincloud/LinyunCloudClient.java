package cn.cyberpeace.mp.flow.integration.cloud.lincloud;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractGetVirtualMachineParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.AddVirtualNicParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.AddVirtualNicResult;
import cn.cyberpeace.mp.flow.integration.cloud.base.DeleteVirtualMachineResult;
import cn.cyberpeace.mp.flow.integration.cloud.base.GetVirtualMachineResult;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

@Slf4j
public class LinyunCloudClient implements ICloudClient {

    private static final String VIRTUAL_MACHINE_GET_URL = "/v1/instances/{instance_id}";

    private static final String ADD_VIRTUAL_NIC_URL = "/cloud/v1/vm/{instance_id}/network";

    private static final String VIRTUAL_MACHINE_CREATE_URL = "/cloud/v1/scenarios/vm";

    private static final String VIRTUAL_MACHINE_DELETE_URL = "/cloud/v1/scenarios/vm";

    private String cloudIp;

    private Integer cloudPort;

    private String cloudHost;

    public LinyunCloudClient(String cloudIp, Integer cloudPort) {
        this.cloudIp = cloudIp;
        this.cloudPort = cloudPort;
        this.cloudHost = "http://" + cloudIp + ":" + cloudPort;
    }

    @Override
    public GetVirtualMachineResult getVirtualMachine(String id) {
        LinyunGetVirtualMachineParams snParams = new LinyunGetVirtualMachineParams();
        snParams.setId(id);
        String url = this.cloudHost + VIRTUAL_MACHINE_GET_URL;
        url.replace("{instance_id}", snParams.getId());
        try {
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
        LinyunCreateVirtualMachineParams snParams = LinyunCreateVirtualMachineParams.getDefault();
        String url = this.cloudHost + VIRTUAL_MACHINE_CREATE_URL;
        try {
            var resp = HttpUtils.sendPost(url, JsonCommon.snakeStringify(snParams));
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
    public DeleteVirtualMachineResult deleteVirtualMachine(String id) {
        LinyunDeleteVirtualMachineParams params = new LinyunDeleteVirtualMachineParams();
        params.setVms(Arrays.asList(id));
        String url = this.cloudHost + VIRTUAL_MACHINE_DELETE_URL;
        try {
            var resp = HttpUtils.sendDelete(url, JsonCommon.snakeStringify(params));
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
        VirtualMachineAddPortData addPortData = new VirtualMachineAddPortData();
        addPortData.setName("mp_" + System.currentTimeMillis());
        addPortData.setNetworkId(params.getNetworkId());
        addPortData.setHwVifModel("virtio");

        try {
            String url = ADD_VIRTUAL_NIC_URL.replace("{instanceId}", params.getInstanceId());
            url = this.cloudHost + url;
            var addTrexClientPortRsp = HttpUtils.sendHttpPost(url, JsonCommon.snakeStringify(addPortData), new HashMap<>());
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

    @Setter
    @Getter
    private static class VirtualMachineAddPortData {
        private String id;
        private String name;
        private String networkId;
        private String fixedIp;
        private String hwVifModel;
    }

}
