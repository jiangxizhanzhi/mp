package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/11
 */
public class ConnectedNetworkBO {
    CloudPluginSceneElementNetworkBO network;

    List<ConnectPort> connectPorts;

    HandlerUtils handlerUtils;

    public ConnectedNetworkBO(CloudPluginSceneElementNetworkBO network, HandlerUtils handlerUtils) {
        this.network = network;
        this.connectPorts = new ArrayList<>();
        this.handlerUtils = handlerUtils;
    }

    public CloudPluginSceneElementNetworkBO getNetwork() {
        return network;
    }

    public void addPort(SceneElementPortBO from, SceneElementPortBO to, boolean create) {
        this.connectPorts.add(new ConnectPort(from, to));
        if (create) {
            handlerUtils.getPluginHandler().getPort(from.getId()).setLogicNetworkId(this.network.getId());
        }
        handlerUtils.addNetwork(from, network);
    }

    public List<SceneElementPortBO> getFromPorts() {
        List<SceneElementPortBO> fromPorts = new ArrayList<>();
        connectPorts.forEach(cp -> {
            if (cp.from != null) {
                fromPorts.add(cp.from);
            }
        });
        return fromPorts;
    }

    public List<SceneElementPortBO> getToPorts() {
        List<SceneElementPortBO> toPorts = new ArrayList<>();
        connectPorts.forEach(cp -> {
            if (cp.to != null) {
                toPorts.add(cp.to);
            }
        });
        return toPorts;
    }

    public static class ConnectPort {
        SceneElementPortBO from;
        SceneElementPortBO to;

        public ConnectPort(SceneElementPortBO from, SceneElementPortBO to) {
            this.from = from;
            this.to = to;
        }
    }
}
