package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.QosRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateHandler extends HandlerCommon {

    private final VirtualSwitch vsClient = new VirtualSwitch();
    private final VirtualRouter vrClient = new VirtualRouter();
    private final VirtualFirewall vfClient = new VirtualFirewall();
    private final VirtualMachine vmClient = new VirtualMachine();
    private final Container containerClient = new Container();
    private final EntityNode enClient = new EntityNode();

    private void createAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
        var createData = new VirtualSwitchCreateData();
        createData.setName(genResourceName(cloudElement.getSrcElement().getEid()));
        createData.setEntityNetwork(cloudElement.getAuxiliaryNetworkVlan());
        createData.setSegmentationId(cloudElement.getVlanId());
        var subnet = new VirtualSwitchCreateData.Subnet();
        subnet.setCidr(cloudElement.getAuxiliaryNetworkCidr());
        subnet.setGatewayIp(cloudElement.getAuxiliaryNetworkGateway());
        subnet.setEnableDhcp(cloudElement.getAuxiliaryNetworkDhcp());
        var srcAllocationPools = cloudElement.getAuxiliaryNetworkAllocationPools();
        if (srcAllocationPools != null) {
            List<VirtualSwitchCreateData.Subnet.AllocationPool> allocationPools = new ArrayList<>();
            srcAllocationPools.forEach(srcAllocationPool -> {
                allocationPools.add(new VirtualSwitchCreateData.Subnet.AllocationPool(srcAllocationPool.getStart(),
                        srcAllocationPool.getEnd()));
            });
            subnet.setAllocationPools(allocationPools);
        }
        subnet.setDnsNameservers(cloudElement.getAuxiliaryNetworkDnsNameservers());
        createData.setSubnet(subnet);
        vsClient.create(createData);
    }

    private List<VirtualNic> genVirutalNics(CloudPluginSceneElementBO cloudElement) {
        List<VirtualNic> virtualNics = new ArrayList<>();
        var srcElement = cloudElement.getSrcElement();
        var ports = srcElement.getPorts();
        if (ports == null) {
            return virtualNics;
        }

        for (var port : ports) {
            var cloudPort = getPluginHandler().getPort(port.getId());
            var connectedSrcElement = getPluginHandler().getPortConnectedSrcElement(port.getId());
            if (connectedSrcElement == null) {
                continue;
            }
            var connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
            var auxiliaryNetworkCloudElement = confirmAuxiliaryNetworkCloudElement(cloudElement, connectedCloudElement);
            if (auxiliaryNetworkCloudElement == null) {
                continue;
            }
            var virtualNic = new VirtualNic();
            virtualNic.setNetworkId(auxiliaryNetworkCloudElement.getAuxiliaryNetworkId());
            virtualNic.setFixedIp(port.getIp());

            if (port.getIngress() > 0 || port.getDelay() > 0 || port.getLoss() > 0) {
                var qosRule = new VirtualNic.QosRule();
                qosRule.setMaxKbps(port.getIngress());
                qosRule.setDirection(QosRuleDirection.INGRESS);
                qosRule.setDelayMs(port.getDelay());
                qosRule.setLossPercent(port.getLoss());
                virtualNic.setQosRule(qosRule);
            }
            if (port.getEgress() > 0 || port.getDelay() > 0 || port.getLoss() > 0) {
                var qosRule = new VirtualNic.QosRule();
                qosRule.setMaxKbps(port.getEgress());
                qosRule.setDirection(QosRuleDirection.EGRESS);
                qosRule.setDelayMs(port.getDelay());
                qosRule.setLossPercent(port.getLoss());
                virtualNic.setQosRule(qosRule);
            }

            if (cloudPort != null) {
                virtualNic.setEnableExternal(cloudPort.getNat());
            }

            // TODO
            virtualNic.setFirewallId(null);

            var srcAccessModes = port.getAccessModes();
            if (srcAccessModes != null) {
                virtualNic.setAccessModes(srcAccessModes.stream().map(srcAccessMode -> {
                    var accessMode = new VirtualNic.AccessMode();
                    accessMode.setProtocol(srcAccessMode.getProtocol());
                    accessMode.setPort(srcAccessMode.getPort());
                    accessMode.setUsername(srcAccessMode.getUsername());
                    accessMode.setPassword(srcAccessMode.getPassword());
                    accessMode.setEnablePortMapping(srcAccessMode.getProxy());
                    accessMode.setPortMappingProtocol(srcAccessMode.getBaseProtocol());
                    accessMode.setIpAddress(port.getIp());
                    return accessMode;
                }).collect(Collectors.toList()));
            }
            virtualNics.add(virtualNic);
        }

        return virtualNics;
    }

    private List<EntityNodeCreateData.Interface> genEntityInterfaces(CloudPluginSceneElementBO cloudElement) {
        List<EntityNodeCreateData.Interface> entityInterfaces = new ArrayList<>();
        var srcElement = cloudElement.getSrcElement();
        var ports = srcElement.getPorts();
        if (ports == null) {
            return entityInterfaces;
        }

        for (var port : ports) {
            var connectedSrcElement = getPluginHandler().getPortConnectedSrcElement(port.getId());
            if (connectedSrcElement == null) {
                continue;
            }
            var connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
            var auxiliaryNetworkCloudElement = confirmAuxiliaryNetworkCloudElement(cloudElement, connectedCloudElement);
            if (auxiliaryNetworkCloudElement == null) {
                continue;
            }
            var entityInterface = new EntityNodeCreateData.Interface();
            entityInterface.setNetworkId(auxiliaryNetworkCloudElement.getAuxiliaryNetworkId());
            entityInterface.setInterfaceName(port.getName());

            var srcAccessModes = port.getAccessModes();
            if (srcAccessModes != null) {
                entityInterface.setAccessModes(srcAccessModes.stream().map(srcAccessMode -> {
                    var accessMode = new EntityNodeCreateData.Interface.AccessMode();
                    accessMode.setProtocol(srcAccessMode.getProtocol());
                    accessMode.setPort(srcAccessMode.getPort());
                    accessMode.setUsername(srcAccessMode.getUsername());
                    accessMode.setPassword(srcAccessMode.getPassword());
                    accessMode.setIpAddress(port.getIp());
                    accessMode.setMacAddress(port.getMacAddr());
                    return accessMode;
                }).collect(Collectors.toList()));
            }
            entityInterfaces.add(entityInterface);
        }

        return entityInterfaces;
    }

    private void createVirtualRouter(CloudPluginSceneElementBO cloudElement) {
        var createData = new VirtualRouterCreateData();
        createData.setName(genResourceName(cloudElement.getSrcElement().getEid()));
        var srcStaticRoutes = cloudElement.getStaticRoutes();
        if (srcStaticRoutes != null) {
            List<VirtualRouterCreateData.StaticRoute> staticRoutes = new ArrayList<>();
            srcStaticRoutes.forEach(srcStaticRoute -> {
                staticRoutes.add(new VirtualRouterCreateData.StaticRoute(srcStaticRoute.getDestination(),
                        srcStaticRoute.getNexthop()));
            });
            createData.setRoutes(staticRoutes);
        }
        createData.setNetworks(genVirutalNics(cloudElement));
        vrClient.create(createData);
    }

    private void createVirtualFirewall(CloudPluginSceneElementBO cloudElement) {
        var createData = new VirtualFirewallCreateData();
        createData.setName(genResourceName(cloudElement.getSrcElement().getEid()));
        var srcIngressRules = cloudElement.getFirewallIngressRules();
        if (srcIngressRules != null) {
            List<VirtualFirewallCreateData.Rule> ingressRules = new ArrayList<>();
            srcIngressRules.forEach(srcIngressRule -> {
                ingressRules.add(new VirtualFirewallCreateData.Rule(srcIngressRule.getSourceIpAddress(),
                        srcIngressRule.getDestinationIpAddress(), srcIngressRule.getSourcePort(),
                        srcIngressRule.getDestinationPort(), null, srcIngressRule.getProtocol(),
                        srcIngressRule.getAction()));
            });
            createData.setIngressRules(ingressRules);
        }
        var srcEgressRules = cloudElement.getFirewallEgressRules();
        if (srcEgressRules != null) {
            List<VirtualFirewallCreateData.Rule> egressRules = new ArrayList<>();
            srcEgressRules.forEach(srcEgressRule -> {
                egressRules.add(new VirtualFirewallCreateData.Rule(srcEgressRule.getSourceIpAddress(),
                        srcEgressRule.getDestinationIpAddress(), srcEgressRule.getSourcePort(),
                        srcEgressRule.getDestinationPort(), null, srcEgressRule.getProtocol(),
                        srcEgressRule.getAction()));
            });
            createData.setEgressRules(egressRules);
        }
        // TODO 端口
        vfClient.create(createData);
    }

    private void createVirtualMachine(CloudPluginSceneElementBO cloudElement) {
        var srcElement = cloudElement.getSrcElement();
        var createData = new VirtualMachineCreateData();
        createData.setName(genResourceName(srcElement.getEid()));
        createData.setImage(cloudElement.getImage());
        createData.setCpu(srcElement.getCpu());
        createData.setRam(srcElement.getRam());
        createData.setNetworks(genVirutalNics(cloudElement));
        vmClient.create(createData);
    }

    private void createContainer(CloudPluginSceneElementBO cloudElement) {
        var srcElement = cloudElement.getSrcElement();
        var createData = new ContainerCreateData();
        createData.setName(genResourceName(srcElement.getEid()));
        createData.setImage(cloudElement.getImage());
        createData.setCpu(srcElement.getCpu());
        createData.setRam(srcElement.getRam());
        createData.setNetworks(genVirutalNics(cloudElement));
        containerClient.create(createData);
    }

    private void createEntityNode(CloudPluginSceneElementBO cloudElement) {
        var createData = new EntityNodeCreateData();
        createData.setName(cloudElement.getImage());
        createData.setInterfaces(genEntityInterfaces(cloudElement));
        enClient.create(createData);
    }

    private void createElement(CloudPluginSceneElementBO cloudElement) {
        if (Boolean.TRUE.equals(cloudElement.getExternal())) {
            return;
        }
        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            return;
        }

        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            cloudElement.setResourceId(cloudElement.getAuxiliaryNetworkId());
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            createVirtualRouter(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            createVirtualFirewall(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            createVirtualMachine(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            createContainer(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            createEntityNode(cloudElement);
        }
    }

    public CreateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler);
    }

    public void create() {
        // 先创建辅助网络(包含虚拟交换机)
        for (var cloudElement : getPluginHandler().getElementMap().values()) {
            if (Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
                createAuxiliaryNetwork(cloudElement);
            }
        }

        // 再创建网关
        for (var cloudElement : getPluginHandler().getGatewayElementMap().values()) {
            createElement(cloudElement);
        }

        // 再创建防火墙
        for (var cloudElement : getPluginHandler().getFirewallElementMap().values()) {
            createElement(cloudElement);
        }

        // 再创建终端
        for (var cloudElement : getPluginHandler().getTerminalElementMap().values()) {
            createElement(cloudElement);
        }
    }
}
