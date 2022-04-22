package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.constant.CommonConstant;
import cn.cyberpeace.mp.aggregation.constant.TargetConstant;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.AclsInfo;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.FirewallRule;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.StaticRoute;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleAction;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.constant.IpVersion;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 将解析出的node节点转成BO
 * @Author maojun
 * @Date 2021/10/12
 */
class NodeElementConverter implements IConverter {
    private List<NodeElementParseResult> parseResult;

    public NodeElementConverter(List<NodeElementParseResult> parseResult) {
        this.parseResult = parseResult;
    }

    public void convert2BO(ConvertDataContainer container) {
        if (CollectionUtils.isNotEmpty(this.parseResult)) {
            this.parseResult.stream().forEach(node -> this.createElementAndPort(node, container));
        }
    }

    private void createElementAndPort(NodeElementParseResult node, ConvertDataContainer container) {
        // 主表
        SceneConfigElementBO sceneConfigElementBO = new SceneConfigElementBO();
        sceneConfigElementBO.setSystemType("306");
        sceneConfigElementBO.setSystemArchitecture(BaseSceneElementModel.SystemArchitecture.BIT_64);
        sceneConfigElementBO.setSystemLanguage(BaseSceneElementModel.SystemLanguage.EN);
        container.addNodeElement(sceneConfigElementBO);

        sceneConfigElementBO.setId(node.getId());
        sceneConfigElementBO.setName(node.getName());
        if (CollectionUtils.isNotEmpty(node.getInitCommands())) {
            List<String> initCommands = node.getInitCommands().stream()
                    .filter(command -> command != null)
                    .map(command -> "n:" + command.getO())
                    .collect(Collectors.toList());
            sceneConfigElementBO.setInitCommands(initCommands);
        }
        if (node.getId().startsWith("server")) {
            sceneConfigElementBO.setType(BaseSceneElementModel.Type.TERMINAL);
        } else {
            sceneConfigElementBO.setType(BaseSceneElementModel.Type.ROUTER);
        }
        List<SceneConfigElementPortBO> portBOS = new ArrayList<>();
        sceneConfigElementBO.setPorts(portBOS);

        // 插件表
        CloudPluginSceneConfigElementBO pluginElement = new CloudPluginSceneConfigElementBO();
        pluginElement.setImageType(BaseCloudPluginSceneElementModel.ImageType.SIMPLE);
        pluginElement.setImage(node.getImage());
        // 初步加载role imageType (部分简单靶标不会导入), 这里不一定准确
        if (node.getId().toLowerCase().startsWith("router")) {
            sceneConfigElementBO.setIcon2(CommonConstant.DefaultImageUrl.ROUTER);
            pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER);
            pluginElement.setImageType(BaseCloudPluginSceneElementModel.ImageType.SIMPLE);
            if (StringUtils.isEmpty(node.getImage()) || node.getImage().equals("默认路由")) {
                pluginElement.setImage(TargetConstant.DefaultTargetName.DEFAULT_ROUTER);
            }
        } else if (node.getId().toLowerCase().startsWith("firewall")) {
            sceneConfigElementBO.setIcon2(CommonConstant.DefaultImageUrl.FIREWALL);
            pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL);
            pluginElement.setImageType(BaseCloudPluginSceneElementModel.ImageType.SIMPLE);
            if (StringUtils.isEmpty(node.getImage()) || node.getImage().equals("默认防火墙")) {
                pluginElement.setImage(TargetConstant.DefaultTargetName.DEFAULT_FIREWALL);
            }
        } else if (node.getId().toLowerCase().startsWith("server")) {
            sceneConfigElementBO.setIcon2(CommonConstant.DefaultImageUrl.SERVER);
            pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL);
            pluginElement.setImageType(BaseCloudPluginSceneElementModel.ImageType.VM);
        }
        container.putPluginElement(sceneConfigElementBO.getId(), pluginElement);

        // 路由器加入 static-routes
        if (CollectionUtils.isNotEmpty(node.getStaticRoutes())) {
            List<StaticRoute> routes = node.getStaticRoutes().stream()
                    .map(s -> {
                        StaticRoute staticRoute = new StaticRoute();
                        staticRoute.setDestination(s.getDestination());
                        staticRoute.setNexthop(s.getGateway());
                        return staticRoute;
                    })
                    .collect(Collectors.toList());
            pluginElement.setStaticRoutes(routes);
        }

        if (CollectionUtils.isEmpty(node.getNet())) {
            // 节点未连接任何网络
            container.addNoNetworkNodeElements(sceneConfigElementBO);
        } else {
            for (NetElementParseResult ymlNetParseResult : node.getNet()) {
                SceneConfigElementPortBO portBO = new SceneConfigElementPortBO();
                portBO.setIp(ymlNetParseResult.getIp());
                portBO.setId(IConverter.getPortId(sceneConfigElementBO.getId(), sceneConfigElementBO.getType(), portBOS.size()));
                portBOS.add(portBO);
                container.putNode2NetworkPorts(node.getId(), ymlNetParseResult.getId(), portBO);

                CloudPluginSceneConfigElementPortBO pluginPort = new CloudPluginSceneConfigElementPortBO();
                if (CollectionUtils.isNotEmpty(ymlNetParseResult.getAcls())) {
                    pluginPort.setFirewallRules(this.buildFirewallRules(ymlNetParseResult.getAcls()));
                }
                container.putPluginElementPort(portBO.getId(), pluginPort);
            }
        }
    }

    private List<FirewallRule> buildFirewallRules(List<AclsInfo> aclsInfos) {
        return aclsInfos.stream().map(aclsInfo -> {
            FirewallRule firewallRule = new FirewallRule();
            // ip与端口
            firewallRule.setSourceIpAddress(aclsInfo.getSourceIP());
            firewallRule.setSourcePort(aclsInfo.getSourcePort());
            firewallRule.setDestinationIpAddress(aclsInfo.getTargetIP());
            firewallRule.setDestinationPort(aclsInfo.getTargetPort());
            // 协议
            firewallRule.setProtocol(aclsInfo.getProtocol());
            String ipStr = StringUtils.isNotEmpty(aclsInfo.getSourceIP()) ? aclsInfo.getSourceIP() : aclsInfo.getTargetIP();
            if (StringUtils.isEmpty(ipStr) || ipStr.length() <= 15) {
                firewallRule.setIpVersion(IpVersion.IPV4);
            } else {
                firewallRule.setIpVersion(IpVersion.IPV6);
            }
            // 方向
            if ("up".equals(aclsInfo.getDirection())) {
                firewallRule.setDirection(FirewallRuleDirection.EGRESS);
            } else if ("down".equals(aclsInfo.getDirection())) {
                firewallRule.setDirection(FirewallRuleDirection.INGRESS);
            } else if ("two-way".equals(aclsInfo.getDirection())) {
                firewallRule.setDirection(FirewallRuleDirection.BOTH);
            }
            // 规则
            if ("deny".equals(aclsInfo.getAction())) {
                firewallRule.setAction(FirewallRuleAction.DENY);
            } else if ("allow".equals(aclsInfo.getAction())) {
                firewallRule.setAction(FirewallRuleAction.ALLOW);
            } else if ("reject".equals(aclsInfo.getAction())) {
                firewallRule.setAction(FirewallRuleAction.REJECT);
            }
            return firewallRule;
        }).collect(Collectors.toList());
    }
}
