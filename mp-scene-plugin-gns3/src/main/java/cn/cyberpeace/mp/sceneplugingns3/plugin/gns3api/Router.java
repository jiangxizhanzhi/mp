package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api;

import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3UrlConstant;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.common.Gns3Client;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.router.UpdateRouterData;

import java.util.List;

public class Router extends Gns3Client {
    public void update(String projectId, String nodeId, int dynamipsId, List<UpdateRouterData.Port> ports, List<UpdateRouterData.StaticRoute> routes) {
        String url = Gns3UrlConstant.UPDATE_ROUTER_CONFIG.replace("{project_id}", projectId);
        url = url.replace("{node_id}", nodeId);
        url = url.replace("{dynamips_id}", String.valueOf(dynamipsId));

        UpdateRouterData updateRouterData = new UpdateRouterData();
        updateRouterData.setPorts(ports);
        updateRouterData.setStaticRoutes(routes);
        try {
            getHttpClient().post(url, updateRouterData, null, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
