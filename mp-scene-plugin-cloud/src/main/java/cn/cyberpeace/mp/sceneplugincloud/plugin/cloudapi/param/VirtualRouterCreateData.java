package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName VirtualRouterCreateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualRouterCreateData implements CloudHttpData {
    private String name;
    private List<StaticRoute> routeStatics;
    private List<VirtualRouterNic> networks;
    private List<VirtualNic> network;

    private String id;
    private ExternalGatewayInfo externalGatewayInfo;

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
