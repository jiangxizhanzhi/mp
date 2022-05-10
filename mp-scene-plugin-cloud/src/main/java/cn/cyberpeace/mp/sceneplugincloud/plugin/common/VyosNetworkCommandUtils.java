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

    /**
     * 设置静态路由
     */
    private static class StaticRouteCmdBuilder implements NetworkingCmdBuilder {
        private static String SET_NEXT_HOP = "set protocols static route %s next-hop %s ";
        private static String SET_NEXT_HOP_INTERFACE = "next-hop-interface eth%s";

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
            return cmds;
        }
    }

    /**
     * 设置OSPF网络
     */
    private static class OSPFCmdBuilder implements NetworkingCmdBuilder {

        private static String SET_INTERFACES = "set interfaces ethernet eth%s address %s";
        private static String SET_AREAS = "set protocols ospf area %s network %s";

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
                            String.format(SET_AREAS, p.getOspfArea(), IpUtils.getFormatCidrIp(p.getOspfIp()))
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
        private static String SET_AS = "set protocols bgp %s address-family ipv4-unicast network %s";
        private static String SET_NEIGHBOR_BFD = "set protocols bgp %s neighbor %s bfd";
        private static String SET_NEIGHBOR_AS = "set protocols bgp %s neighbor %s remote-as %s";

        @Override
        public List<String> build(ConfigParamsWrapper paramsWrapper) {
            if (CollectionUtils.isEmpty(paramsWrapper.pluginPorts)) {
                return new ArrayList<>();
            }
            List<String> cmds = new ArrayList<>();
            paramsWrapper.pluginPorts.forEach(p -> {
                if (StringUtils.isNotEmpty(p.getBgpAs())) {
                    cmds.add(
                            String.format(SET_INTERFACE_ADDRESS, p.getSrcPort().getOrder(), p.getBgpIp())
                    );
                    cmds.add(
                            String.format(SET_PEER, p.getBgpPeer())
                    );
                    cmds.add(
                            String.format(SET_AS, p.getBgpAs(), IpUtils.getFormatCidrIp(p.getBgpIp()))
                    );
                    cmds.add(
                            String.format(SET_NEIGHBOR_BFD, p.getBgpAs(), p.getBgpPeer())
                    );
                    cmds.add(
                            String.format(SET_NEIGHBOR_AS, p.getBgpAs(), p.getBgpPeer(), p.getBgpPeerAs())
                    );
                }
            });
            return cmds;
        }
    }
}
