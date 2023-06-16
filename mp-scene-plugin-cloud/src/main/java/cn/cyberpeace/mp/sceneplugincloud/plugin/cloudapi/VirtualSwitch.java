package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.PortMappingCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.PortMappingCreateResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchUpdateData;
import cn.cyberpeace.mp.sceneplugincloud.util.ForceReleaseResourceUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VirtualSwitch extends CloudClient {

    private static final Logger log = LoggerFactory.getLogger(VirtualSwitch.class);

    public VirtualSwitchCreateData create(VirtualSwitchCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualSwitchCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_SWITCH_CREATE_URL, createData,
                null, new TypeReference<>() {
                }, true);
        return result.getData();
    }

    public boolean delete(VirtualSwitchDeleteData deleteData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.VIRTUAL_SWITCH_DELETE_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    public void update(String resourceId, VirtualSwitchUpdateData updateData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudPatch(CloudUrlConstant.VIRTUAL_SWITCH_UPDATE_URL.replace("{network_id}", resourceId), updateData, null, new TypeReference<>() {
        }, true);
    }

    public boolean forceDelete(VirtualSwitchDeleteData deleteData) {
        ForceReleaseResourceUtils.forceDeleteNetwork(deleteData.getIds());
        return true;
    }

    public PortMappingCreateResult createPortMapping(PortMappingCreateData portMappingCreateData) {
        var result = getHttpClient().post(CloudUrlConstant.CREATE_PORT_MAPPING, portMappingCreateData, null,
                PortMappingCreateResult.class);

        log.info("portMapping result is {}", JsonCommon.stringify(result));
        return result;
    }

    @Getter
    @Setter
    private static class NetworkParam implements CloudHttpData {
        private List<String> networkIds;
    }
}
