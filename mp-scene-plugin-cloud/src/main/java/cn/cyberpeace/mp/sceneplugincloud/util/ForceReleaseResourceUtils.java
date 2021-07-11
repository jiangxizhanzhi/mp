package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class ForceReleaseResourceUtils {
    public static void forceDeleteVm(List<String> resourceIds) {
        VmParam param = new VmParam();
        param.setInstanceIds(resourceIds);
        new Remote().cloudDelete(CloudUrlConstant.FORCE_DELETE_VM, param, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    public static void forceDeleteContainer(List<String> resourceIds) {
        ContainerParam param = new ContainerParam();
        param.setContainerIds(resourceIds);
        new Remote().cloudDelete(CloudUrlConstant.FORCE_DELETE_CONTAINER, param, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    public static void forceDeleteNetwork(List<String> resourceIds) {
        NetworkParam param = new NetworkParam();
        param.setNetworkIds(resourceIds);
        new Remote().cloudDelete(CloudUrlConstant.FORCE_DELETE_NETWORK, param, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    public static void forceDeleteRouter(List<String> resourceIds) {
        RouterParam param = new RouterParam();
        param.setRouterIds(resourceIds);
        new Remote().cloudDelete(CloudUrlConstant.FORCE_DELETE_ROUTER, param, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    @Getter
    @Setter
    private static class ContainerParam implements CloudHttpData {
        private List<String> containerIds;
    }

    @Getter
    @Setter
    private static class NetworkParam implements CloudHttpData {
        private List<String> networkIds;
    }

    @Getter
    @Setter
    private static class VmParam implements CloudHttpData {
        private List<String> instanceIds;
    }

    @Getter
    @Setter
    private static class RouterParam implements CloudHttpData {
        private List<String> routerIds;
    }
}
