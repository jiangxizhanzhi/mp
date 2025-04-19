package cn.cyberpeace.mp.flow.integration.cloud.lincloud;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.integration.cloud.base.*;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

@Slf4j
@Service("linyunCloudClient")
public class LinyunCloudClient implements ICloudClient {

    private static final String VIRTUAL_MACHINE_GET_URL = "/v1/instances/{instance_id}";

    private static final String ADD_VIRTUAL_NIC_URL = "/cloud/v1/vm/{instance_id}/network";

    private static final String VIRTUAL_MACHINE_CREATE_URL = "/cloud/v1/scenarios/vm";

    private static final String VIRTUAL_MACHINE_DELETE_URL = "/cloud/v1/scenarios/vm";

    private String cloudHost;

    public LinyunCloudClient(@Autowired FlowConfig flowConfig) {
        this.cloudHost = flowConfig.getCpUrl();
        log.info("使用 linyunCloudClient , cloud host : {}", this.cloudHost);
    }

    @Override
    public GetVirtualMachineResult getVirtualMachine(String id) {
        LinyunGetVirtualMachineParams snParams = new LinyunGetVirtualMachineParams();
        snParams.setId(id);
        String url = this.cloudHost + VIRTUAL_MACHINE_GET_URL;
        url = url.replace("{instance_id}", snParams.getId());
        try {
            log.info("查询虚拟机信息 , url is : {}", url);
            var resp = HttpUtils.sendGet(url);
            log.info("结果: {}", resp);
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
            var params = JsonCommon.snakeStringify(snParams);
            log.info("创建虚拟机 , url is : {} , params is : {}", url, params);
            var resp = HttpUtils.sendPost(url, params);
            log.info("结果: {}", resp);
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
            var strParams = JsonCommon.snakeStringify(params);
            log.info("删除虚拟机 , url is : {} , params is : {}", url, strParams);
            var resp = HttpUtils.sendDelete(url, strParams);
            log.info("结果: {}", resp);
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
            String url = ADD_VIRTUAL_NIC_URL.replace("{instance_id}", params.getInstanceId());
            url = this.cloudHost + url;
            var strParams = JsonCommon.snakeStringify(addPortData);
            log.info("添加网口 ， url is : {} , params is : {}", url, strParams);
            var addTrexClientPortRsp = HttpUtils.sendHttpPost(url, strParams, new HashMap<>());
            log.info("结果: {}", addTrexClientPortRsp);
            CloudResponse<AddVirtualNicResponse> response = JsonCommon.snakeParse(addTrexClientPortRsp, new TypeReference<CloudResponse<AddVirtualNicResponse>>() {
            });
            if (!"0".equals(response.getCode()) || response.getData() == null) {
                log.error("failed to add nic , instacne {} , network {} , result {}", params.getInstanceId(), params.getNetworkId(), addTrexClientPortRsp);
                throw new RuntimeException("add nic error");
            }
            AddVirtualNicResult result = new AddVirtualNicResult();
            result.setMacAddress(response.getData().getMac());
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
        private String mac;
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
