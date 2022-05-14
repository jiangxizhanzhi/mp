package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.StaticRoute;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2022/4/20
 */
public class VyosNetworkCommandUtils {

    private static List<NetworkingCmdBuilder> builderChains = new ArrayList<>() {{
        this.add(new StaticRouteCmdBuilder());
        this.add(new OSPFCmdBuilder());
        this.add(new BGPCmdBuilder());
//        this.add(new RouterNameCmdBuilder());
    }};

    public static List<String> genOpenDhcpCommands() {
        return List.of("set interface ethernet eth0 address dhcp");
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
                    String.format(SET_HOST_NAME, paramsWrapper.element.getSrcElement().getName())
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
                    result.add(
                            String.format(SET_INTERFACES, p.getSrcPort().getOrder(), p.getOspfIp())
                    );
                    setAreas.add(
                            String.format(SET_AREA, p.getOspfArea(), IpUtils.getFormatCidrIp(p.getOspfIp()))
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
                    // 端口有bgp配置 根据配置创建命令
                    String interfaceIp = p.getSrcPort().getIp() + "/" + p.getBgpIp().split("/")[1];
                    cmds.addAll(List.of(new String[]{
                            String.format(SET_INTERFACE_ADDRESS, p.getSrcPort().getOrder(), interfaceIp),
                            String.format(SET_PEER, p.getBgpPeer()),
                            String.format(SET_DECLARE_NET, p.getBgpAs(), IpUtils.getFormatCidrIp(p.getBgpIp())),
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
}
