package cn.cyberpeace.mp.sceneplugingns3.controller;

import cn.cyberpeace.mp.sceneplugingns3.constant.ControllerConstant;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Cloud;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Link;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Project;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Router;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Switch;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.router.UpdateRouterData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(value = "场景流量插件", tags = "场景流量插件")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH)
@Data
public class Gns3PluginFlowController {

    @ApiOperation("场景模板流量插件数据，非API")
    @GetMapping("/test")
    public String loadSceneConfig() {
        return null;
//        Project project = new Project();
//        var projectId = project.create("mj_interface004");
//
//        Cloud cloud = new Cloud();
//        var cloudId = cloud.create(projectId, "cloud");
//
//        Router router = new Router();
//        var routerId = router.create(projectId, "router");
//        List<UpdateRouterData.Port> ports = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            UpdateRouterData.Port port = new UpdateRouterData.Port();
//            port.setIp("192.168." + (i + 1) + ".254");
//            port.setTag(100 + i);
//            ports.add(port);
//        }
//        List<UpdateRouterData.StaticRoute> routes = new ArrayList<>();
//        router.update(projectId, routerId, 1, ports, routes);
//
//        Switch swit = new Switch();
//        var switchId = swit.create(projectId, "switch", List.of(100, 101));
//        Link link = new Link();
//        link.link(projectId, cloudId, 0, switchId, 0);
//        link.link(projectId, switchId, 1, routerId, 0);
//
//        router.start(projectId, routerId);
//        cloud.start(projectId, cloudId);
//        swit.start(projectId, switchId);
//
//        return projectId;

//        Project project = new Project();
//        String projectId = project.create("mj_test002");
//
//        Cloud cloud = new Cloud();
//        String cloudNode = cloud.create(projectId, "mj_test01");
//
//        Switch swit = new Switch();
//        String switchNode = swit.create(projectId, "mj_test03", 317);
//
//        VPC vpc = new VPC();
//        String vpcNode = vpc.create(projectId, "mj_test02");
//
//        UpdateVpcsData vpcsData = new UpdateVpcsData();
//        vpcsData.setGateway("172.217.89.254");
//        vpcsData.setIp("172.217.89.10/24");
//        vpc.update(projectId, vpcNode, vpcsData);
//
//        Router router = new Router();
//        var routerNode = router.create(projectId, "mj_test05");
//        UpdateRouterData.Port port1 = new UpdateRouterData.Port();
//        port1.setTag(100);
//        port1.setIp("192.168.1.5");
//
//        UpdateRouterData.Port port2 = new UpdateRouterData.Port();
//        port2.setTag(101);
//        port2.setIp("192.168.2.5");
//
//        router.update(projectId, routerNode, List.of(port1, port2));
//
//        Link link = new Link();
//        link.link(projectId, cloudNode, 0, switchNode, 0);
//        link.link(projectId, switchNode, 1, vpcNode, 0);
//
//        cloud.start(projectId, cloudNode);
//        swit.start(projectId, switchNode);
//        vpc.start(projectId, vpcNode);
//        router.start(projectId, routerNode);
//
//        return projectId;
    }
}