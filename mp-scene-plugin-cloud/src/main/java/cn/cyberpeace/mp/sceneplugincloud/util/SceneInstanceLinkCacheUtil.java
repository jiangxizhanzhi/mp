package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeLinkStatusBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/1
 */
public class SceneInstanceLinkCacheUtil {
    private static LoadingCache<String, List<NodeLinkStatusBO>> cache = CacheBuilder.newBuilder()
            .maximumSize(300)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public List<NodeLinkStatusBO> load(String sceneId) {
                    SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
                    CloudScenePluginHandler handler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
                    List<NodeLinkStatusBO> nodeLinkStatusList = new ArrayList<>();
                    sceneHandler.getElementMap().values().forEach(e -> {
                        var cloudElement = handler.getElement(e.getId());
                        Integer virtualType = cloudElement.getVirtualType();
                        if (virtualType != null && (
                                virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE
                        )) {
                            NodeLinkStatusBO nodeStatus = new NodeLinkStatusBO(e.getId());
                            nodeStatus.setResourceId(handler.getElement(e.getId()).getResourceId());
                            nodeLinkStatusList.add(nodeStatus);
                            if (CollectionUtils.isNotEmpty(e.getPorts())) {
                                for (var p : e.getPorts()) {
                                    var connectPort = sceneHandler.getPortIdConnectedPortIdMap().get(p.getId());
                                    if (connectPort != null) {
                                        String key = StringUtils.joinWith(":", p.getId(), connectPort);
                                        SceneElementPortConnectionBO connection = sceneHandler.getElementPortConnectionMap().get(key);
                                        if (connection == null) {
                                            key = StringUtils.joinWith(":", connectPort, p.getId());
                                            connection = sceneHandler.getElementPortConnectionMap().get(key);
                                        }
                                        if (connection != null) {
                                            NodeLinkStatusBO.ConnectionStatus connectionStatus = new NodeLinkStatusBO.ConnectionStatus(connection.getId());
                                            connectionStatus.setNetworkId(handler.getPort(p.getId()).getAuxiliaryNetworkId());
                                            nodeStatus.getConnectionStatusList().add(connectionStatus);
                                        }
                                    }
                                }
                            }
                        }
                    });
                    return nodeLinkStatusList;
                }
            });

    public static List<NodeLinkStatusBO> getCloned(String sceneId) {
        var cached = cache.getUnchecked(sceneId);
        List<NodeLinkStatusBO> list = new ArrayList<>();
        for (var node : cached) {
            NodeLinkStatusBO clonedNode = new NodeLinkStatusBO(node.getNodeId());
            clonedNode.setResourceId(node.getResourceId());
            for (var port : node.getConnectionStatusList()) {
                NodeLinkStatusBO.ConnectionStatus clonedPort = new NodeLinkStatusBO.ConnectionStatus(port.getConnectionId());
                clonedPort.setNetworkId(port.getNetworkId());
                clonedNode.getConnectionStatusList().add(clonedPort);
            }
            list.add(clonedNode);
        }
        return cache.getUnchecked(sceneId);
    }

    public static void clear(String sceneId) {
        cache.invalidate(sceneId);
    }
}
