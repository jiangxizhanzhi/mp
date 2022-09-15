package cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugingns3.plugin.Gns3ScenePluginHandler;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Cloud;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Link;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Project;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Router;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Switch;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.router.RouterNodeCreateData;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.router.RouterProperties;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.router.UpdateRouterData;
import cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import cn.hutool.core.net.Ipv4Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CreateHandler extends HandlerCommon {

    private Project project = new Project();
    private Cloud cloud = new Cloud();
    private Switch swit = new Switch();
    private Router router = new Router();
    private Link link = new Link();

    public static class SceneProcess {
        public static final EventProcess CREATE_GNS3NODES = new EventProcess("create_gns3nodes", "sceneplugingns3.event.create_gns3nodes");
    }

    public static class ElementProcess {
        public static final EventProcess CREATE_ROUTER = new EventProcess("create_router", "sceneplugingns3.event.create_router");
    }


    public CreateHandler(Gns3ScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

        // 创建gns3的节点
        runSceneProgress(SceneProcess.CREATE_GNS3NODES, () -> {
            CloudScenePluginHandler cloudPlugin = getPluginHandler().getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
            getHandlerUtils().loadSwitchPorts(cloudPlugin);

            String bridgeName = getPluginHandler().getSourceSceneId().substring(0, 5);
            var projectId = project.create(getPluginHandler().getSourceSceneId());
            project.createBridge(bridgeName);
            getPluginHandler().getScene().setResourceId(projectId);

            var cloudId = cloud.create(projectId, bridgeName);
            getPluginHandler().getScene().setBridgeResourceId(cloudId);

            var switchPort = getHandlerUtils().addDot1qSwitchPort();
            var switchId = swit.create(projectId, "switch" + getPluginHandler().getSourceSceneId(), getHandlerUtils().getSwitchPorts());
            getHandlerUtils().setSwitchId(switchId);

            link.link(projectId, getPluginHandler().getScene().getBridgeResourceId(), 0, switchId, switchPort.getPortNumber());

            List<Runnable> tasks = getPluginHandler().getElementMap().values()
                    .stream()
                    .filter(e -> e.getSrcElement().getType() == BaseSceneElementModel.Type.ROUTER)
                    .map(e -> createRouterProcess(e))
                    .collect(Collectors.toList());
//            runTasks(tasks);
            tasks.forEach(t -> t.run());
            project.start(projectId);
        });
    }

    private Runnable createRouterProcess(Gns3PluginSceneElementBO e) {
        return () -> runElementProgress(e.getSrcElement().getId(), ElementProcess.CREATE_ROUTER,
                () -> createRouter(e)
        );
    }

    private void createRouter(Gns3PluginSceneElementBO e) {
        CloudScenePluginHandler cloudPlugin = getPluginHandler().getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        var cloudElement = cloudPlugin.getElement(e.getSrcElement().getId());
        var deriveNetworks = cloudElement.getDeriveNetworks();
        var accessNetworks = cloudElement.getAccessNetworks();
        List<ConnectedNetworkBO> allnets = new ArrayList<>();
        allnets.addAll(deriveNetworks);
        allnets.addAll(accessNetworks);

        List<UpdateRouterData.Port> ports = new ArrayList<>();
        for (var net : allnets) {
            UpdateRouterData.Port port = new UpdateRouterData.Port();
            port.setIp(net.getFromPorts().get(0).getIp());
            port.setTag(net.getNetwork().getVlanId());
            ports.add(port);
        }
        List<UpdateRouterData.StaticRoute> routes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(e.getStaticRoutes())) {
            for (var r : e.getStaticRoutes()) {
                UpdateRouterData.StaticRoute route = new UpdateRouterData.StaticRoute();
                route.setNexthop(r.getNexthop());
                this.loadInfo(route, r.getDestination());
                routes.add(route);
            }
        }

        String projectId = getPluginHandler().getScene().getResourceId();

        RouterNodeCreateData routerNodeCreateData = new RouterNodeCreateData(e.getSrcElement().getEid());
        RouterProperties properties = new RouterProperties();
        properties.setImage(e.getImage());
        properties.setPlatform(e.getPlatform());
        properties.setRam(e.getRam());
        properties.setDynamipsId(e.getDynamicId());
        routerNodeCreateData.setProperties(properties);

        var routerId = router.create(projectId, routerNodeCreateData);
        e.setResourceId(routerId);
        router.update(projectId, routerId, e.getDynamicId(), ports, routes);

        link.link(projectId, getHandlerUtils().getSwitchId(), getHandlerUtils().getRouter2SwitchPort().get(e.getSrcElement().getId()).getPortNumber(), routerId, 0);
    }

    private void loadInfo(UpdateRouterData.StaticRoute route, String ip) {
        String[] info = StringUtils.split(ip, "/");
        String ipAddr = info[0];
        int maskBit = Integer.parseInt(info[1]);
        route.setIp(ipAddr);
        route.setMask(Ipv4Util.getMaskByMaskBit(maskBit));
    }
}
