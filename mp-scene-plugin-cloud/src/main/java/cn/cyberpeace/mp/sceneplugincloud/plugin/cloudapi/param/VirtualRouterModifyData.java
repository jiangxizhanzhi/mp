package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualRouterModifyData implements CloudHttpData {
    private String name;
    private List<StaticRoute> routes;

    private String id;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StaticRoute {
        private String destination;
        private String nexthop;
    }
}