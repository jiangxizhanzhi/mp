package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualRouterCreateData implements CloudHttpData {
    private String name;
    private List<StaticRoute> routes;
    private List<VirtualNic> networks;

    private String id;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StaticRoute {
        private String destination;
        private String nexthop;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExternalGatewayInfo {
        private String networkId;
        private String fixedIp;
    }
}
