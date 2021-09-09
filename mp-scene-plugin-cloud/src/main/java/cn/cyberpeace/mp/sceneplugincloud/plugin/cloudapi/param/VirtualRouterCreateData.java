package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.HttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualRouterCreateData implements HttpData {
    private String name;
    private List<StaticRoute> routes;
    private List<VirtualNic> networks;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StaticRoute {
        private String destination;
        private String nexthop;
    }
}
