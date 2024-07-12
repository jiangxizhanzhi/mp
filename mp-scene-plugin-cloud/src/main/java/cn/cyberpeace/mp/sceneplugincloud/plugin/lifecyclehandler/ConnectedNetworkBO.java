 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ConnectedNetworkBO.java.
 // File size 2637.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ConnectedNetworkBO
 * @Description
 * @Author maojun
 * @Date 2022/3/11
 * @Version 1.0.0
 */
public class ConnectedNetworkBO {
    CloudPluginSceneElementNetworkBO network;

    List<ConnectPort> connectPorts;

    HandlerUtils handlerUtils;

    /**
     * ConnectedNetworkBO
     *
     * @param network
     * @param handlerUtils
     */
    public ConnectedNetworkBO(CloudPluginSceneElementNetworkBO network, HandlerUtils handlerUtils) {
        this.network = network;
        this.connectPorts = new ArrayList<>();
        this.handlerUtils = handlerUtils;
    }

    /**
     * getNetwork
     *
     * @return
     */
    public CloudPluginSceneElementNetworkBO getNetwork() {
        return network;
    }

    /**
     * addPort
     *
     * @param from
     * @param to
     * @param create
     */
    public void addPort(SceneElementPortBO from, SceneElementPortBO to, boolean create) {
        this.connectPorts.add(new ConnectPort(from, to));
        if (create) {
            handlerUtils.getPluginHandler().getPort(from.getId()).setLogicNetworkId(this.network.getId());
        }
        handlerUtils.addNetwork(from, network);
    }

    /**
     * getFromPorts
     *
     * @return
     */
    public List<SceneElementPortBO> getFromPorts() {
        List<SceneElementPortBO> fromPorts = new ArrayList<>();
        connectPorts.forEach(cp -> {
            if (cp.from != null) {
                fromPorts.add(cp.from);
            }
        });
        return fromPorts;
    }

    /**
     * getToPorts
     *
     * @return
     */
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

        /**
         * ConnectPort
         *
         * @param from
         * @param to
         */
        public ConnectPort(SceneElementPortBO from, SceneElementPortBO to) {
            this.from = from;
            this.to = to;
        }
    }
}
