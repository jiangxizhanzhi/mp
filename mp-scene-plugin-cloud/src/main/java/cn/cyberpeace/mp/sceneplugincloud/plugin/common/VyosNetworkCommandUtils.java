package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.StaticRoute;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class VyosNetworkCommandUtils {

    private static List<NetworkingCmdBuilder> builderChains = new ArrayList<>();
    private static List<NetworkingCmdBuilder> modifyBuilderChains = new ArrayList<>();

    static {
        builderChains.add(new InterfaceIpSetCmdBuilder());
        builderChains.add(new StaticRouteCmdBuilder());
        builderChains.add(new OSPFCmdBuilder());
        builderChains.add(new BGPCmdBuilder());
        builderChains.add(new RIPCmdBuilder());
        builderChains.add(new QosCmdBuilder());
        builderChains.add(new RouterNameCmdBuilder());
    }

    static {
        modifyBuilderChains.add(new InterfaceIpSetCmdBuilder());
        modifyBuilderChains.add(new StaticRouteCmdBuilder());
        modifyBuilderChains.add(new OSPFCmdBuilder());
        modifyBuilderChains.add(new BGPCmdBuilder());
        modifyBuilderChains.add(new RIPCmdBuilder());
        modifyBuilderChains.add(new QosCmdBuilder());
    }

    public static List<String> genOpenDhcpCommands(List<SceneElementPortBO> ports,HandlerUtils handlerUtils) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < ports.size(); i++) {
            var net = handlerUtils.getNetworkByPort(ports.get(i));
            if (net != null && !Boolean.FALSE.equals(net.getDhcp())) {
                result.add("set interface ethernet eth" + i + " address dhcp");
            }
        }
        return result;
    }

    public static List<String> genNetworkCmdList(CloudPluginSceneElementBO element, CloudScenePluginHandler pluginHandler) {
        ConfigParamsWrapper paramsWrapper = new ConfigParamsWrapper(element, pluginHandler);
        List<String> result = new ArrayList<>();
        for (var builder : builderChains) {
            List<String> cmds = builder.build(paramsWrapper);
            result.addAll(cmds);
        }
        return result;
    }

    public static List<String> genModifyNetworkCmdList(CloudPluginSceneElementBO element, CloudScenePluginHandler pluginHandler) {
        ConfigParamsWrapper paramsWrapper = new ConfigParamsWrapper(element, pluginHandler);
        List<String> result = new ArrayList<>();
        for (var builder : modifyBuilderChains) {
            List<String> cmds = builder.build(paramsWrapper);
            result.addAll(cmds);
        }
        return result;
    }

    private static class InterfaceIpSetCmdBuilder implements NetworkingCmdBuilder {

        private static String SET_INTERFACES = "set interfaces ethernet eth%s address %s";

        @Override
        public List<String> build(ConfigParamsWrapper paramsWrapper) {
            if (CollectionUtils.isEmpty(paramsWrapper.pluginPorts)) {
                return new ArrayList<>();
            }
            List<String> result = new ArrayList<>();
            paramsWrapper.pluginPorts.forEach(p -> {
                boolean noOspf = StringUtils.isEmpty(p.getOspfIp());
                boolean noRip = StringUtils.isEmpty(p.getRipIp());
                boolean noBgp = StringUtils.isEmpty(p.getBgpIp());
                if (noOspf && noBgp && noRip) {
                    var net = paramsWrapper.pluginHandler.getHandlerUtils().getNetworkByPort(p.getSrcPort());
                    if (Objects.nonNull(net)) {
                        String interfaceAddr = p.getSrcPort().getIp() + net.getCidr().substring(net.getCidr().indexOf("/"));
                        result.add(
                                String.format(SET_INTERFACES, p.getSrcPort().getOrder(), interfaceAddr)
                        );
                    }
                }
            });
            return result;
        }
    }

    private static class ConfigParamsWrapper {
        CloudPluginSceneElementBO element;
        CloudScenePluginHandler pluginHandler;
        List<CloudPluginSceneElementPortBO> pluginPorts;
        List<SceneElementPortBO> ports;

        public ConfigParamsWrapper(CloudPluginSceneElementBO element, CloudScenePluginHandler pluginHandler) {
            this.element = element;
            this.pluginHandler = pluginHandler;
            this.ports = element.getSrcElement().getPorts();
            if (CollectionUtils.isEmpty(element.getSrcElement().getPorts())) {
                this.pluginPorts = new ArrayList<>();
            } else {
                pluginPorts = element.getSrcElement().getPorts().stream()
                        .map(p -> pluginHandler.getPort(p.getId()))
                        .filter(p -> p != null)
                        .collect(Collectors.toList());
            }
        }
    }

    private interface NetworkingCmdBuilder {
        List<String> build(ConfigParamsWrapper paramsWrapper);
    }

    private static class RouterNameCmdBuilder implements NetworkingCmdBuilder {
        private String SET_HOST_NAME = "set system host-name %s";

        @Override
        public List<String> build(ConfigParamsWrapper paramsWrapper) {
            return List.of(
                    String.format(SET_HOST_NAME, BeanCommon.getBean(CloudAppConfig.class).getDefaultCoreRouterName())
            );
        }
    }

    
    private static class StaticRouteCmdBuilder implements NetworkingCmdBuilder {
        private static String SET_NEXT_HOP = "set protocols static route %s next-hop %s ";
        private static String SET_NEXT_HOP_INTERFACE = "next-hop-interface eth%s";
        private static String SET_ROUTER_ID = "set protocols ospf parameters router-id %s";

        @Override
        public List<String> build(ConfigParamsWrapper paramsWrapper) {
            CloudPluginSceneElementBO element = paramsWrapper.element;
            var ports = element.getSrcElement().getPorts();
            Map<String, Integer> portId2Order = new HashMap<>();
            ports.forEach(p -> portId2Order.put(p.getId(), p.getOrder()));

            List<String> cmds = new ArrayList<>();
            for (StaticRoute staticRoute : element.getStaticRoutes()) {
                String cmd = String.format(SET_NEXT_HOP, staticRoute.getDestination(), staticRoute.getNexthop());
                if (StringUtils.isNotEmpty(staticRoute.getNexthopPortId()) && portId2Order.containsKey(staticRoute.getNexthopPortId())) {
                    cmd += String.format(SET_NEXT_HOP_INTERFACE, portId2Order.get(staticRoute.getNexthopPortId()));
                }
                cmds.add(cmd);
            }
            if (StringUtils.isNotEmpty(paramsWrapper.element.getOspfRouterId())) {
                cmds.add(
                        String.format(SET_ROUTER_ID, paramsWrapper.element.getOspfRouterId())
                );
            }
            return cmds;
        }
    }

    
    private static class OSPFCmdBuilder implements NetworkingCmdBuilder {

        private static String SET_INTERFACES = "set interfaces ethernet eth%s address %s";
        private static String SET_AREA = "set protocols ospf area %s network %s";

        @Override
        public List<String> build(ConfigParamsWrapper paramsWrapper) {
            if (CollectionUtils.isEmpty(paramsWrapper.pluginPorts)) {
                return new ArrayList<>();
            }
            List<String> result = new ArrayList<>();
            List<String> setAreas = new ArrayList<>();
            paramsWrapper.pluginPorts.forEach(p -> {
                if (StringUtils.isNotEmpty(p.getOspfIp()) && StringUtils.isNotEmpty(p.getOspfArea())) {
                    String ospfIp = p.getOspfIp();
                    if (!ospfIp.contains("/")) {
                        var net = paramsWrapper.pluginHandler.getHandlerUtils().getNetworkByPort(p.getSrcPort());
                        if (net != null && StringUtils.isNotEmpty(net.getCidr()) && net.getCidr().contains("/")) {
                            ospfIp = ospfIp + net.getCidr().substring(net.getCidr().indexOf("/"));
                        } else {
                            ospfIp = ospfIp + "/24";
                        }
                    }
                    result.add(
                            String.format(SET_INTERFACES, p.getSrcPort().getOrder(), ospfIp)
                    );
                    setAreas.add(
                            String.format(SET_AREA, p.getOspfArea(), IpUtils.getFormatCidrIp(ospfIp))
                    );
                }
            });
            result.addAll(setAreas);
            return result;
        }
    }

    
    private static class BGPCmdBuilder implements NetworkingCmdBuilder {
        private static String SET_INTERFACE_ADDRESS = "set interfaces ethernet eth%s address %s";
        private static String SET_PEER = "set protocols bfd peer %s";
        private static String SET_DECLARE_NET = "set protocols bgp %s address-family ipv4-unicast network %s";
        private static String SET_NEIGHBOR_BFD = "set protocols bgp %s neighbor %s bfd";
        private static String SET_NEIGHBOR_AS = "set protocols bgp %s neighbor %s remote-as %s";

        @Override
        public List<String> build(ConfigParamsWrapper paramsWrapper) {
            if (CollectionUtils.isEmpty(paramsWrapper.pluginPorts)) {
                return new ArrayList<>();
            }
            var bgpPorts = paramsWrapper.pluginPorts.stream()
                    .filter(p -> StringUtils.isNotEmpty(p.getBgpAs()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(bgpPorts)) {
                return new ArrayList<>();
            }
            String defaultSelfAs = bgpPorts.get(0).getBgpAs();
            List<String> cmds = new ArrayList<>();
            paramsWrapper.pluginPorts.forEach(p -> {
                if (bgpPorts.contains(p)) {
                    
                    String interfaceIp = null;
                    var network = paramsWrapper.pluginHandler.getHandlerUtils().getNetworkByPort(p.getSrcPort());
                    if (network != null && StringUtils.isNotEmpty(network.getCidr())) {
                        interfaceIp = p.getSrcPort().getIp() + "/" + network.getCidr().split("/")[1];
                    } else {
                        interfaceIp = p.getSrcPort().getIp() + "/24";
                    }
                    cmds.addAll(List.of(new String[]{
                            String.format(SET_INTERFACE_ADDRESS, p.getSrcPort().getOrder(), interfaceIp),
                            String.format(SET_PEER, p.getBgpPeer()),
                            String.format(SET_DECLARE_NET, p.getBgpAs(), IpUtils.getFormatCidrIp(interfaceIp)),
                            String.format(SET_NEIGHBOR_BFD, p.getBgpAs(), p.getBgpPeer()),
                            String.format(SET_NEIGHBOR_AS, p.getBgpAs(), p.getBgpPeer(), p.getBgpPeerAs())
                    }));
                } else if (!Boolean.TRUE.equals(p.getSrcPort().getIsAuxiliary())) {
                    
                    String interfaceIp = null;
                    var network = paramsWrapper.pluginHandler.getHandlerUtils().getNetworkByPort(p.getSrcPort());
                    if (network != null && StringUtils.isNotEmpty(network.getCidr())) {
                        interfaceIp = p.getSrcPort().getIp() + "/" + network.getCidr().split("/")[1];
                    } else {
                        interfaceIp = p.getSrcPort().getIp() + "/24";
                    }
                    cmds.addAll(List.of(new String[]{
                            String.format(SET_INTERFACE_ADDRESS, p.getSrcPort().getOrder(), interfaceIp),
                            String.format(SET_DECLARE_NET, defaultSelfAs, IpUtils.getFormatCidrIp(interfaceIp))
                    }));
                }
            });
            return cmds;
        }
    }

    private static class RIPCmdBuilder implements NetworkingCmdBuilder {

        private String LOOPBACK = "set interfaces loopback lo address %s";
        private String NETWORK = "set protocols rip network %s";
        
        private String NEIGHBOOR = "set protocols rip neighbor %s";

        private String REDISTRIBUTE = "set protocols rip redistribute connected";

        @Override
        public List<String> build(ConfigParamsWrapper paramsWrapper) {
            if (CollectionUtils.isEmpty(paramsWrapper.pluginPorts)) {
                return new ArrayList<>();
            }
            var ripPorts = paramsWrapper.pluginPorts.stream()
                    .filter(p -> StringUtils.isNotEmpty(p.getRipIp()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(ripPorts)) {
                return new ArrayList<>();
            }
            List<String> cmds = new ArrayList<>();
            if (StringUtils.isNotEmpty(paramsWrapper.element.getRipLoopback())) {
                cmds.add(String.format(LOOPBACK, paramsWrapper.element.getRipLoopback()));
            }
            paramsWrapper.pluginPorts.forEach(p -> {
                if (ripPorts.contains(p)) {
                    
                    if (StringUtils.isNotEmpty(p.getRipIp())) {
                        var network = paramsWrapper.pluginHandler.getHandlerUtils().getNetworkByPort(p.getSrcPort());
                        String interfaceIp = null;






                        if (network != null && StringUtils.isNotEmpty(network.getCidr())) {
                            interfaceIp = p.getRipIp() + "/" + network.getCidr().split("/")[1];
                        } else {
                            interfaceIp = p.getRipIp() + "/24";
                        }

                        interfaceIp = IpUtils.getFormatCidrIp(interfaceIp);
                        cmds.add(String.format(NETWORK, interfaceIp));
                    }
                    if (StringUtils.isNotEmpty(p.getRipPear())) {
                        String ripPear = p.getRipPear();
                        if (p.getRipPear().contains("/")) {
                            ripPear = p.getRipPear().split("/")[0];
                        }
                        cmds.add(String.format(NEIGHBOOR, ripPear));
                    }
                } else if (!Boolean.TRUE.equals(p.getSrcPort().getIsAuxiliary())) {
                    
                }
            });
            cmds.add(REDISTRIBUTE);
            return cmds;
        }
    }

    private static class QosCmdBuilder implements NetworkingCmdBuilder{

        
        private String NETWORK_DELAY = "set traffic-policy network-emulator snpolicy%s network-delay %s";
        
        private String NETWORK_PACKET_LOSS = "set traffic-policy network-emulator snpolicy%s packet-loss %s";

        
        private String BIND_OUT_TRAFFIC_POLICY = "set interfaces ethernet eth%s traffic-policy out snpolicy%s";

        @Override
        public List<String> build(ConfigParamsWrapper paramsWrapper) {
            if (CollectionUtils.isEmpty(paramsWrapper.pluginPorts)) {
                return new ArrayList<>();
            }
            List<String> result = new ArrayList<>();
            paramsWrapper.pluginPorts.forEach(p -> {
                SceneElementPortBO srcPort = p.getSrcPort();
                if (Objects.nonNull(srcPort)) {
                    if (srcPort.getDelay() == null) {
                        srcPort.setDelay(-1);
                    }
                    if (srcPort.getLoss() == null) {
                        srcPort.setLoss(-1);
                    }
                    Integer delay = srcPort.getDelay();
                    Integer loss = srcPort.getLoss();
                    Integer order = srcPort.getOrder();
                    String delayCmd = null;
                    String lossCmd = null;
                    if (delay > 0) {
                        
                        delayCmd = String.format(NETWORK_DELAY, order, delay);
                        result.add(delayCmd);
                    }
                    if (loss > 0) {
                        
                        lossCmd = String.format(NETWORK_PACKET_LOSS, order, loss);
                        result.add(lossCmd);
                    }
                    if (!StringUtils.isAllEmpty(delayCmd, lossCmd)) {
                        result.add(String.format(BIND_OUT_TRAFFIC_POLICY, order, order));
                    }
                }
            });
            return result;
        }
    }

    public static boolean isQosChange(CloudPluginSceneElementBO originElement, CloudScenePluginHandler originPluginHandler, CloudPluginSceneElementBO newElement, CloudScenePluginHandler newPluginHandler) {
        var originWrapper = new ConfigParamsWrapper(originElement, originPluginHandler);
        var newWrapper = new ConfigParamsWrapper(newElement, newPluginHandler);
        Set<String> originKeys = new HashSet<>();
        Set<String> newKeys = new HashSet<>();
        if (CollectionUtils.isNotEmpty(originWrapper.pluginPorts)) {
            var ports = originWrapper.pluginPorts;
            for (var p : ports) {
                Integer delay = p.getSrcPort().getDelay();
                Integer loss = p.getSrcPort().getLoss();
                originKeys.add(p.getSrcPort().getId() + "_" + (delay == null ? "0" : delay) + "_" + (loss == null ? "0" : loss));
            }
        }
        if (CollectionUtils.isNotEmpty(newWrapper.pluginPorts)) {
            var ports = newWrapper.pluginPorts;
            for (var p : ports) {
                Integer delay = p.getSrcPort().getDelay();
                Integer loss = p.getSrcPort().getLoss();
                newKeys.add(p.getSrcPort().getId() + "_" + (delay == null ? "0" : delay) + "_" + (loss == null ? "0" : loss));
            }
        }
        if (originKeys.size() != newKeys.size()) {
            return true;
        }
        originKeys.removeAll(newKeys);
        return originKeys.size() != 0;
    }

    public static boolean isProtocolsChange(CloudPluginSceneElementBO originElement, CloudScenePluginHandler originPluginHandler, CloudPluginSceneElementBO newElement, CloudScenePluginHandler newPluginHandler) {
        return isProtocolsChange(new ConfigParamsWrapper(originElement, originPluginHandler),
                new ConfigParamsWrapper(newElement, newPluginHandler));
    }

    private static boolean isProtocolsChange(ConfigParamsWrapper originWrapper, ConfigParamsWrapper newWrapper) {
        var originCloudElement = originWrapper.element;
        var newCloudElement = newWrapper.element;
        if (!StringUtils.equals(originCloudElement.getOspfRouterId(), newCloudElement.getOspfRouterId())) {
            return true;
        }
        var originPorts = originWrapper.pluginPorts;
        var newPorts = newWrapper.pluginPorts;
        return isProtocolsChange(originPorts, newPorts);
    }

    private static boolean isProtocolsChange(List<CloudPluginSceneElementPortBO> originPorts, List<CloudPluginSceneElementPortBO> newPorts) {
        if (CollectionUtils.isEmpty(originPorts) && CollectionUtils.isEmpty(newPorts)) {
            
            return false;
        } else if (CollectionUtils.isEmpty(originPorts) || CollectionUtils.isEmpty(newPorts)) {
            
            return true;
        } else if (originPorts.size() != newPorts.size()) {
            
            return true;
        } else {
            
            Set<String> originKeys = keys(originPorts);
            Set<String> newKeys = keys(newPorts);
            if (originKeys.size() != newKeys.size()) {
                return true;
            }
            originKeys.removeAll(newKeys);
            if (originKeys.size() > 0) {
                return true;
            }
            return false;
        }
    }

    private static Set<String> keys(List<CloudPluginSceneElementPortBO> ports) {
        Set<String> keys = new HashSet<>();
        for (var p : ports) {
            keys.add(ospfKey(p));
            keys.add(ripKey(p));
            keys.add(bgpKey(p));
        }
        return keys;
    }

    private static String ripKey(CloudPluginSceneElementPortBO portBO) {
        var result = Arrays.asList("rip",
                Optional.ofNullable(portBO.getRipIp()).orElse(StringUtils.EMPTY),
                Optional.ofNullable(portBO.getRipPear()).orElse(StringUtils.EMPTY));
        return StringUtils.join(result, "_");
    }


    private static String ospfKey(CloudPluginSceneElementPortBO portBO) {
        var result = Arrays.asList("ospf",
                Optional.ofNullable(portBO.getOspfArea()).orElse(StringUtils.EMPTY),
                Optional.ofNullable(portBO.getOspfIp()).orElse(StringUtils.EMPTY));
        return StringUtils.join(result, "_");
    }

    private static String bgpKey(CloudPluginSceneElementPortBO portBO) {
        var result = Arrays.asList("bgp",
                Optional.ofNullable(portBO.getBgpAs()).orElse(StringUtils.EMPTY),
                Optional.ofNullable(portBO.getBgpPeer()).orElse(StringUtils.EMPTY),
                Optional.ofNullable(portBO.getBgpIp()).orElse(StringUtils.EMPTY),
                Optional.ofNullable(portBO.getBgpPeerAs()).orElse(StringUtils.EMPTY));
        return StringUtils.join(result, "_");
    }
}
