package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.ForceReleaseResourceUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

public class VirtualSwitch extends CloudClient {

    public VirtualSwitch() {
        CloudHttpClient cloudHttpClient = new CloudHttpClient(CloudSingleton.cloudAppConfig.getCpcloudServer(),
                CloudSingleton.cloudAppConfig.getCpcloudServerTimeout(),
                CloudSingleton.cloudAppConfig.getCpcloudHttpPoolSize());
        this.setHttpClient(cloudHttpClient);
    }

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

    public boolean forceDelete(VirtualSwitchDeleteData deleteData) {
        ForceReleaseResourceUtils.forceDeleteNetwork(deleteData.getIds());
        return true;
    }

    @Getter
    @Setter
    private static class NetworkParam implements CloudHttpData {
        private List<String> networkIds;
    }
}
