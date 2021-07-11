package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.constant.CommonConstant;
import cn.cyberpeace.mp.aggregation.constant.TargetConstant;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.AclsInfo;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
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
import cn.hutool.core.io.FileUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
class NodeElementConverter implements IConverter {

    private static String FDFS_SCENE_FILEPATH_FORMAT = "%s?filename=%s";

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
        
        SceneConfigElementBO sceneConfigElementBO = new SceneConfigElementBO();
        sceneConfigElementBO.setSystemType("306");
        sceneConfigElementBO.setSystemArchitecture(BaseSceneElementModel.SystemArchitecture.BIT_64);
        sceneConfigElementBO.setSystemLanguage(BaseSceneElementModel.SystemLanguage.EN);
        container.addNodeElement(sceneConfigElementBO);

        if (StringUtils.isNotEmpty(node.getPosition())) {
            String[] xy = node.getPosition().split(",");
            sceneConfigElementBO.setPosition2(JsonCommon.stringify(new Origin(formatDouble(xy[0]), formatDouble(xy[1]))));
        }

        sceneConfigElementBO.setId(node.getId());
        sceneConfigElementBO.setName(node.getName());
        if (CollectionUtils.isNotEmpty(node.getInitCommands())) {
            List<String> result = new ArrayList<>();
            for (var initCommand : node.getInitCommands()) {
                if (initCommand != null) {
                    result.addAll(initCommand.getAllCommands());
                }
            }
            sceneConfigElementBO.setInitCommands(result);
        }
        if (node.getId().startsWith("server")) {
            sceneConfigElementBO.setType(BaseSceneElementModel.Type.TERMINAL);
        } else {
            sceneConfigElementBO.setType(BaseSceneElementModel.Type.ROUTER);
        }
        
        sceneConfigElementBO.setDisplayContext(node.getDisplayContext());
        List<SceneConfigElementPortBO> portBOS = new ArrayList<>();
        sceneConfigElementBO.setPorts(portBOS);
        
        if (StringUtils.isNotEmpty(node.getFile())) {
            
            if (FileUtil.exist(node.getFile())) {
                String fdfsPath = FsCommon.upload(node.getFile());
                String fileName = FileUtil.getName(node.getFile());
                String nodeFilePath = String.format(FDFS_SCENE_FILEPATH_FORMAT, fdfsPath, fileName);
                sceneConfigElementBO.setFile(nodeFilePath);
            } else {
                log.info("import scene , no scene file for path {}", node.getFile());
            }
        }

        
        CloudPluginSceneConfigElementBO pluginElement = new CloudPluginSceneConfigElementBO();
        pluginElement.setImage(node.getImage());
        
        if (node.getId().toLowerCase().startsWith("router")) {
            sceneConfigElementBO.setIcon2(DefaultImgUtil.pathToFdfsUrl(CommonConstant.DefaultImageUrl.ROUTER));
            pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER);
            if (StringUtils.isEmpty(node.getImage()) || node.getImage().equals("默认路由")) {
                pluginElement.setImage(TargetConstant.DefaultTargetName.DEFAULT_ROUTER);
            }
        } else if (node.getId().toLowerCase().startsWith("firewall")) {
            sceneConfigElementBO.setIcon2(DefaultImgUtil.pathToFdfsUrl(CommonConstant.DefaultImageUrl.FIREWALL));
            pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL);
            if (StringUtils.isEmpty(node.getImage()) || node.getImage().equals("默认防火墙")) {
                pluginElement.setImage(TargetConstant.DefaultTargetName.DEFAULT_FIREWALL);
            }
        } else if (node.getId().toLowerCase().startsWith("server")) {
            sceneConfigElementBO.setIcon2(DefaultImgUtil.pathToFdfsUrl(CommonConstant.DefaultImageUrl.SERVER));
            if ("npc".equals(node.getVirtualPlatform())) {
                pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.NPC);
                pluginElement.setImageType(BaseCloudPluginSceneElementModel.ImageType.SIMPLE);
                pluginElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.NPC);
                sceneConfigElementBO.setIcon2(DefaultImgUtil.pathToFdfsUrl(CommonConstant.DefaultImageUrl.NPC));
            } else {
                pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL);
            }
        }
        container.putPluginElement(sceneConfigElementBO.getId(), pluginElement);

        
        if (CollectionUtils.isNotEmpty(node.getUsedStaticRoutes())) {
            List<StaticRoute> routes = node.getUsedStaticRoutes().stream()
                    .map(s -> {
                        StaticRoute staticRoute = new StaticRoute();
                        staticRoute.setDestination(s.getDestination());
                        staticRoute.setNexthop(s.getGateway());
                        staticRoute.setNexthopPortId(s.getNexthopNode());
                        return staticRoute;
                    })
                    .collect(Collectors.toList());
            pluginElement.setStaticRoutes(routes);
        }
        if (node.getExtInfo() != null) {
            container.getExtNodeInfos().put(node.getId(), node.getExtInfo());
        }

        if (CollectionUtils.isEmpty(node.getNet())) {
            
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
                if (StringUtils.startsWith(ymlNetParseResult.getId(), "network") || StringUtils.startsWith(ymlNetParseResult.getId(), "internet")) {
                    continue;
                } else {
                    container.addNode2NodePort(node.getId(), ymlNetParseResult.getId(), portBO);
                }
            }
        }
    }

    private List<FirewallRule> buildFirewallRules(List<AclsInfo> aclsInfos) {
        return aclsInfos.stream().map(aclsInfo -> {
            FirewallRule firewallRule = new FirewallRule();
            
            firewallRule.setSourceIpAddress(aclsInfo.getSourceIP());
            firewallRule.setSourcePort(aclsInfo.getSourcePort());
            firewallRule.setDestinationIpAddress(aclsInfo.getTargetIP());
            firewallRule.setDestinationPort(aclsInfo.getTargetPort());
            
            firewallRule.setProtocol(aclsInfo.getProtocol());
            String ipStr = StringUtils.isNotEmpty(aclsInfo.getSourceIP()) ? aclsInfo.getSourceIP() : aclsInfo.getTargetIP();
            if (StringUtils.isEmpty(ipStr) || ipStr.length() <= 15) {
                firewallRule.setIpVersion(IpVersion.IPV4);
            } else {
                firewallRule.setIpVersion(IpVersion.IPV6);
            }
            
            if ("up".equals(aclsInfo.getDirection())) {
                firewallRule.setDirection(FirewallRuleDirection.EGRESS);
            } else if ("down".equals(aclsInfo.getDirection())) {
                firewallRule.setDirection(FirewallRuleDirection.INGRESS);
            } else if ("two-way".equals(aclsInfo.getDirection())) {
                firewallRule.setDirection(FirewallRuleDirection.BOTH);
            }
            
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

    private double formatDouble(String number) {
        return new BigDecimal(number).setScale(6, RoundingMode.HALF_DOWN).doubleValue();
    }

    @Getter
    @Setter
    private static class Position {
        private Origin origin;
        private double height;
        private double width;
    }

    @Getter
    @Setter
    private static class Origin {
        private double x;
        private double y;

        public Origin(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
