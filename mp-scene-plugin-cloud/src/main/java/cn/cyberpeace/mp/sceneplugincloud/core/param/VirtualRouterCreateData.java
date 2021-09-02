package cn.cyberpeace.mp.sceneplugincloud.core.param;

import cn.cyberpeace.mp.sceneplugincloud.core.common.HttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualRouterCreateData implements HttpData {
    private String name;
    private List<StaticRoute> routes;
    private List<VirtualNic> networks;

    @Data
    public static class StaticRoute {
        private String destination;
        private String nexthop;
    }
}
