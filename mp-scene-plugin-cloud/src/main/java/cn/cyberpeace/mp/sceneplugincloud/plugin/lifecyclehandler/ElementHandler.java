package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.CoreType;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/10
 */
@Slf4j
public class ElementHandler {
    // 节点
    public CloudPluginSceneElementBO element;

    // 加入的网络
    public List<ConnectedNetwork> accessNetworks;

    // 构建的网络
    public List<ConnectedNetwork> deriveNetworks;

    private HandlerUtils handlerUtils;
    private CloudScenePluginHandler pluginHandler;

    public ElementHandler(CloudPluginSceneElementBO element, CloudScenePluginHandler pluginHandler, HandlerUtils handlerUtils) {
        this.element = element;
        this.accessNetworks = new ArrayList<>();
        this.deriveNetworks = new ArrayList<>();
        this.initCoreType();
    }

    public void initCoreType() {
        Integer role = this.element.getRole();
        CoreType coreType = CoreType.NETWORK;
        if (role != null) {
            if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY)
                    || role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)
                    || role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH)
            ) {
                coreType = CoreType.NETWORK;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER)) {
                coreType = CoreType.ROUTER;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL)) {
                coreType = CoreType.FIREWALL;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)) {
                coreType = CoreType.TERMINAL;
            }
        }
        this.element.setCoreType(coreType);
    }

    public List<CloudPluginSceneElementNetworkBO> getDeriveNetworks() {
        return this.deriveNetworks.stream().map(n -> n.network).collect(Collectors.toList());
    }

    public static CoreType getCoreType(Integer role, String imageType) {
        if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY)
                || role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)
                || role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH)
        ) {
            return CoreType.NETWORK;
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER)) {
            return CoreType.ROUTER;
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL)) {
            return CoreType.FIREWALL;
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)) {
            return CoreType.TERMINAL;
        } else {
            return CoreType.NETWORK;
        }
    }

    public static class ConnectedNetwork {
        CloudPluginSceneElementNetworkBO network;

        List<ConnectPort> connectPorts;

        HandlerUtils handlerUtils;

        public ConnectedNetwork(CloudPluginSceneElementNetworkBO network, HandlerUtils handlerUtils) {
            this.network = network;
            this.connectPorts = new ArrayList<>();
            this.handlerUtils = handlerUtils;
        }

        public void addPort(SceneElementPortBO from, SceneElementPortBO to) {
            this.connectPorts.add(new ConnectPort(from, to));
            handlerUtils.addNetwork(from, network);
        }
    }

    public static class ConnectPort {
        SceneElementPortBO from;
        SceneElementPortBO to;

        public ConnectPort(SceneElementPortBO from, SceneElementPortBO to) {
            this.from = from;
            this.to = to;
        }
    }

    public String ip2Cidr24(String ip) {
        return ip.substring(0, ip.lastIndexOf(".")) + ".0/24";
    }
}
