package cn.cyberpeace.mp.flow.integration.cloud.sncloud;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractAddVirtualNicParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractCreateVirtualMachineParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractDeleteVirtualMachineParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.AddVirtualNicResult;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class SaiNingCloudClient implements ICloudClient {

    private static final String ADD_VIRTUAL_NIC_URL = "/v1/instances/{instanceId}/interfaces";

    @Override
    public void createVirtualMachine(AbstractCreateVirtualMachineParams params) {
        SnCreateVirtualMachineParams snParams = (SnCreateVirtualMachineParams) params;
    }

    @Override
    public void deleteVirtualMachine(AbstractDeleteVirtualMachineParams params) {

    }

    @Override
    public AddVirtualNicResult addVirtualNic(AbstractAddVirtualNicParams params) {
        SnAddVirtualNicParams snParams = (SnAddVirtualNicParams) params;
        String url = ADD_VIRTUAL_NIC_URL.replace("{instanceId}", ((SnAddVirtualNicParams) params).getInstanceId());
        Map<String, String> body = Map.of("network_id", snParams.getNetworkId());
        try {
            var addTrexClientPortRsp = HttpUtils.sendHttpPost(url, JsonCommon.snakeStringify(body), null);
            CloudResponse<AddVirtualNicResponse> response = JsonCommon.snakeParse(addTrexClientPortRsp, new TypeReference<CloudResponse<AddVirtualNicResponse>>() {
            });
            if (!"0".equals(response.getCode()) || response.getData() == null) {
                log.error("failed to add nic , instacne {} , network {} , result {}", snParams.getInstanceId(), snParams.getNetworkId(), addTrexClientPortRsp);
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
