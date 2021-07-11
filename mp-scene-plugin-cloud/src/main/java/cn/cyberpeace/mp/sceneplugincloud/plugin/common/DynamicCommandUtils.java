package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.hutool.core.net.Ipv4Util;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DynamicCommandUtils {

    public static List<String> genSetIpCmdList(CloudPluginSceneElementBO element, CloudScenePluginHandler pluginHandler) {
        ConfigParamsWrapper paramsWrapper = new ConfigParamsWrapper(element, pluginHandler);
        return new IPCmdBuilder().build(paramsWrapper);
    }

    public static List<String> genNetworkCmdList(CloudPluginSceneElementBO element, CloudScenePluginHandler pluginHandler) {
        ConfigParamsWrapper paramsWrapper = new ConfigParamsWrapper(element, pluginHandler);
        return new StaticRouteCmdBuilder().build(paramsWrapper);
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
                pluginPorts = element.getSrcElement().getPorts().stream().map(p -> pluginHandler.getPort(p.getId())).filter(p -> p != null).collect(Collectors.toList());
            }
        }
    }

    private interface NetworkingCmdBuilder {
        List<String> build(ConfigParamsWrapper paramsWrapper);
    }

    private static class IPCmdBuilder implements NetworkingCmdBuilder {

        private static String ENABLE = "enable";
        private static String CONFIGURE = "conf t";
        private static String CHOOSE_INT = "int ${interfaceName}";
        private static String SET_IP = "ip add ${ip_addr} ${mask}";
        private static String START_INT = "no shutdown";
        private static String EXIT = "exit";
        private static String SAVE = "wr";

        @Override
        public List<String> build(ConfigParamsWrapper paramsWrapper) {
            List<String> cmds = new ArrayList<>();
            for (var port : paramsWrapper.ports) {
                if (StringUtils.isNotEmpty(port.getIp()) && paramsWrapper.pluginHandler.getHandlerUtils().getNetworkByPort(port) != null) {
                    cmds.add(CHOOSE_INT.replace("${interfaceName}", port.getName()));
                    var net = paramsWrapper.pluginHandler.getHandlerUtils().getNetworkByPort(port);
                    if (StringUtils.isNotEmpty(net.getCidr())) {
                        IpParser ipParser = new IpParser(net.getCidr(), paramsWrapper.element);
                        cmds.add(SET_IP.replace("${ip_addr}", port.getIp()).replace("${mask}", ipParser.mask));
                    } else {
                        cmds.add(SET_IP.replace("${ip_addr}", port.getIp()).replace("${mask}", "255.255.255.0"));
                    }
                    cmds.add(START_INT);
                    cmds.add(EXIT);
                }
            }
            return cmds;
        }

    }

    
    private static class StaticRouteCmdBuilder implements NetworkingCmdBuilder {

        private static String ENABLE = "enable";
        private static String CONFIGURE = "conf t";
        private static String ADD_ROUTE = "ip route ${dest} ${mask} ${nexthop}";
        private static String EXIT = "exit";
        private static String SAVE = "wr";


        @Override
        public List<String> build(ConfigParamsWrapper paramsWrapper) {
            List<String> cmds = new ArrayList<>();
            for (var staticRoute : paramsWrapper.element.getStaticRoutes()) {
                IpParser ipParser = new IpParser(staticRoute.getDestination(), paramsWrapper.element);
                cmds.add(ADD_ROUTE.replace("${dest}", ipParser.getNet()).replace("${mask}", ipParser.getMask()).replace("${nexthop}", staticRoute.getNexthop()));
            }
            return cmds;
        }

    }

    @Getter
    private static class IpParser {
        private String ipWithMask;
        private String ip;
        private String mask;
        private int maskBit;
        private String net;

        IpParser(String ipWithMask, CloudPluginSceneElementBO elementBO) {
            String elementName = elementBO == null ? "" : elementBO.getSrcElement().getName();
            this.ipWithMask = ipWithMask;
            String[] info = ipWithMask.split("/");
            this.ip = info[0];
            this.maskBit = Integer.parseInt(info[1]);
            this.mask = Ipv4Util.getMaskByMaskBit(this.maskBit);
            try {
                long maskIpLong = Ipv4Util.ipv4ToLong(mask);
                long ipLong = Ipv4Util.ipv4ToLong(ip);
                this.net = Ipv4Util.longToIpv4(ipLong & maskIpLong);
            } catch (Exception e) {
                throw new MpRuntimeException(CloudErrorCode.UN_SUPPORT_STATIC_ROUTE.format(elementName, ipWithMask));
            }
        }

    }

}
