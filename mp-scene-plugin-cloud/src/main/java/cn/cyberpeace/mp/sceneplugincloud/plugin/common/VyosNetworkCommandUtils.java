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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2022/4/20
 */
public class VyosNetworkCommandUtils {

    private static List<NetworkingCmdBuilder> builderChains = new ArrayList<>();

    static {
        builderChains.add(new StaticRouteCmdBuilder());
        builderChains.add(new OSPFCmdBuilder());
        builderChains.add(new BGPCmdBuilder());
        builderChains.add(new RouterNameCmdBuilder());
        builderChains.add(new RIPCmdBuilder());
        builderChains.add(new QosCmdBuilder());
    }

    public static List<String> genOpenDhcpCommands(int ethSize) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < ethSize; i++) {
            result.add("set interface ethernet eth" + i + " address dhcp");
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

    /**
     * 设置静态路由
     */
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

    /**
     * 设置OSPF网络
     */
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

    /**
     * 设置BGP网络
     */
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
                    // 端口有bgp配置 根据配置创建命
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
                    // 非辅助端口 且 bgp无配置 设置下地址,宣告下网络
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
        //        private String RIP_INTERFACE = "set protocols rip interface eth%s";
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
                    // 端口有bgp配置 根据配置创建命令
                    if (StringUtils.isNotEmpty(p.getRipIp())) {
                        var network = paramsWrapper.pluginHandler.getHandlerUtils().getNetworkByPort(p.getSrcPort());
                        String interfaceIp = null;
//                        if (network != null && StringUtils.isNotEmpty(network.getCidr())) {
//                            interfaceIp = p.getSrcPort().getIp() + "/" + network.getCidr().split("/")[1];
//                        } else {
//                            interfaceIp = p.getSrcPort().getIp() + "/24";
//                        }

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
                    // 非辅助端口 且 bgp无配置 设置下地址,宣告下网络
                }
            });
            cmds.add(REDISTRIBUTE);
            return cmds;
        }
    }

    private static class QosCmdBuilder implements NetworkingCmdBuilder{

        //设置时延
        private String NETWORK_DELAY = "set traffic-policy network-emulator snpolicy%s network-delay %s";
        //设置丢包率
        private String NETWORK_PACKET_LOSS = "set traffic-policy network-emulator snpolicy%s packet-loss %s";

        //绑定设置的策略
        private String BIND_IN_TRAFFIC_POLICY = "set interfaces ethernet eth%s traffic-policy in snpolicy%s";

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
                    Integer delay = srcPort.getDelay();
                    Integer loss = srcPort.getLoss();
                    Integer order = srcPort.getOrder();
                    String delayCmd = null;
                    String lossCmd = null;
                    if (delay > 0) {
                        //时延命令
                        delayCmd = String.format(NETWORK_DELAY, order, delay);
                        result.add(delayCmd);
                    }
                    if (loss > 0) {
                        //丢包率命令
                        lossCmd = String.format(NETWORK_PACKET_LOSS, order, loss);
                        result.add(lossCmd);
                    }
                    if (StringUtils.isNoneEmpty(delayCmd, lossCmd)) {
                        result.add(String.format(BIND_IN_TRAFFIC_POLICY, order, order));
                        result.add(String.format(BIND_OUT_TRAFFIC_POLICY, order, order));
                    }
                }
            });
            return result;
        }
    }
}
