package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
@Getter
public class HandlerUtils {

    private CloudScenePluginHandler pluginHandler;

    private final List<String> cidrPool = CloudSingleton.cloudAppConfig.getCidrPool();

    private final Map<String, CloudPluginSceneElementBO> portIdAuxiliaryNetworkCloudElementMap = new HashMap<>();
    private final Map<String, List<String>> networkNodeIdPortIdsMap = new HashMap<>();
    private final Map<String, Set<String>> networkNodeIdOuterExistIpsMap = new HashMap<>();

    public HandlerUtils(CloudScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
    }

    public static boolean isExternalNetwork(String networkId) {
        if (StringUtils.isEmpty(networkId)) {
            return false;
        }
        return networkId.equals(CloudSingleton.cloudAppConfig.getExternalNetId());
    }

    public static String genResourceName(String name) {
        return StringUtils.joinWith("_", CloudSingleton.cloudAppConfig.getCpcloudResourceGroup(), name);
    }

    /**
     * 确认连接使用的辅助网络所在节点
     * @param cloudElement 源节点
     * @param connectedCloudElement 连接节点
     * @return 辅助网络所在节点
     */
    public static CloudPluginSceneElementBO confirmAuxiliaryNetworkCloudElement(
            CloudPluginSceneElementBO cloudElement, CloudPluginSceneElementBO connectedCloudElement) {
        if (cloudElement == null || connectedCloudElement == null) {
            if (cloudElement != null && Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
                return cloudElement;
            }
            if (connectedCloudElement != null && Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
                return connectedCloudElement;
            }
            return null;
        }

        var srcElement = cloudElement.getSrcElement();
        var connectedElement = connectedCloudElement.getSrcElement();
        Integer type = srcElement.getType();
        Integer connectedType = connectedElement.getType();
        Integer virtualType = cloudElement.getVirtualType();
        Integer connectedVirtualType = connectedCloudElement.getVirtualType();

        // 判断是否是默认类型的虚拟交换机(默认包含辅助网络)
        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return cloudElement;
        }
        if (connectedVirtualType != null
                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return connectedCloudElement;
        }

        // 判断是否是交换机 且开启辅助网络
        if (type != null && type.equals(BaseSceneElementModel.Type.SWITCH)
                && Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return cloudElement;
        }
        if (connectedType != null && connectedType.equals(BaseSceneElementModel.Type.SWITCH)
                && Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return connectedCloudElement;
        }

        // 判断是否是默认类型的虚拟路由 且开启辅助网络
        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return cloudElement;
        }
        if (connectedVirtualType != null
                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return connectedCloudElement;
        }

        // 判断是否是路由 且开启辅助网络
        if (type != null && type.equals(BaseSceneElementModel.Type.ROUTER)
                && Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return cloudElement;
        }
        if (connectedType != null && connectedType.equals(BaseSceneElementModel.Type.ROUTER)
                && Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return connectedCloudElement;
        }

        // 判断是否是防火墙 且开启辅助网络
        if (type != null && type.equals(BaseSceneElementModel.Type.FIREWALL)
                && Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return cloudElement;
        }
        if (connectedType != null && connectedType.equals(BaseSceneElementModel.Type.FIREWALL)
                && Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return connectedCloudElement;
        }

        // 判断是否开启辅助网络
        if (Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return cloudElement;
        }
        if (Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return connectedCloudElement;
        }

        return null;
    }

    public String getRequestUser() {
        var context = getPluginHandler().getContext();
        if (context == null) {
            return null;
        }

        return context.getRequestUser();
    }

    public String getRequestUserOrg() {
        var context = getPluginHandler().getContext();
        if (context == null) {
            return null;
        }

        return context.getRequestUserOrg();
    }

    public List<String> randomCidrs(int count, List<String> existCidrs) {
        return IpUtils.randomCidrs(cidrPool, count, existCidrs);
    }

    public List<String> statExistCidrs() {
        List<String> existCidrs = new ArrayList<>();
        getPluginHandler().getElementMap().forEach((nodeId, element) -> {
            var cidr = element.getCidr();
            if (!StringUtils.isEmpty(cidr)) {
                existCidrs.add(cidr);
            }

            var auxiliaryNetworkCidr = element.getAuxiliaryNetworkCidr();
            if (!StringUtils.isEmpty(auxiliaryNetworkCidr)) {
                existCidrs.add(auxiliaryNetworkCidr);
            }
        });
        return existCidrs;
    }

    /**
     * 预加载场景中端口关联的辅助网络节点
     */
    public void loadAuxiliaryNetworkCloudElementPortIdsMap() {
        getPluginHandler().getSceneHandler().getElementPortMap().forEach((portId, srcPort) -> {
            var auxiliaryNetworkCloudElement = getPortAuxiliaryNetworkCloudElement(portId);
            if (auxiliaryNetworkCloudElement != null) {
                var nodeId = auxiliaryNetworkCloudElement.getSrcElement().getId();
                MapUtils.setDefault(networkNodeIdPortIdsMap, nodeId, new ArrayList<>()).add(portId);
            }
        });
    }

    /**
     * 获取辅助网络连接的所有端口
     */
    public synchronized List<String> getAuxiliaryNetworkCloudElementPortIds(CloudPluginSceneElementBO auxiliaryNetworkCloudElement) {
        if (networkNodeIdPortIdsMap.isEmpty()) {
            loadAuxiliaryNetworkCloudElementPortIdsMap();
        }
        return networkNodeIdPortIdsMap.getOrDefault(auxiliaryNetworkCloudElement.getSrcElement().getId(), null);
    }

    public void addOuterExistIps(CloudPluginSceneElementBO auxiliaryNetworkCloudElement, List<String> outerExistIps) {
        var nodeId = auxiliaryNetworkCloudElement.getSrcElement().getId();
        MapUtils.setDefault(networkNodeIdOuterExistIpsMap, nodeId, new HashSet<>()).addAll(outerExistIps);
    }

    public Set<String> getOuterExistIps(CloudPluginSceneElementBO auxiliaryNetworkCloudElement) {
        var nodeId = auxiliaryNetworkCloudElement.getSrcElement().getId();
        return networkNodeIdOuterExistIpsMap.getOrDefault(nodeId, new HashSet<>());
    }

    /**
     * 获取端口所在辅助网络网元
     * @param portId 网元端口id
     * @return 端口所在辅助网络网元
     */
    public CloudPluginSceneElementBO getPortAuxiliaryNetworkCloudElement(String portId) {
        if (portIdAuxiliaryNetworkCloudElementMap.containsKey(portId)) {
            return portIdAuxiliaryNetworkCloudElementMap.get(portId);
        }
        var srcElement = getPluginHandler().getSrcPort(portId).getElement();
        var cloudElement = getPluginHandler().getElement(srcElement.getId());
        var connectedOpaqueSrcPort = getPortConnectedOpaqueSrcPort(portId);
        if (connectedOpaqueSrcPort == null) {
            // 未连接端口情况，如果节点存在其它连接，则忽略该端口
            if (getPluginHandler().getSceneHandler().elementHasConnectedPort(srcElement.getId())) {
                portIdAuxiliaryNetworkCloudElementMap.put(portId, null);
                return null;
            }
        }

        var connectedOpaqueSrcElement = connectedOpaqueSrcPort != null ? getPluginHandler().getPortSrcElement(connectedOpaqueSrcPort.getId()) : null;
        var connectedCloudElement = connectedOpaqueSrcElement != null ? getPluginHandler().getElement(connectedOpaqueSrcElement.getId()) : null;
        var auxiliaryNetworkCloudElement = confirmAuxiliaryNetworkCloudElement(cloudElement, connectedCloudElement);
        portIdAuxiliaryNetworkCloudElementMap.put(portId, auxiliaryNetworkCloudElement);
        if (connectedOpaqueSrcPort != null) {
            portIdAuxiliaryNetworkCloudElementMap.put(connectedOpaqueSrcPort.getId(), auxiliaryNetworkCloudElement);
        }
        return auxiliaryNetworkCloudElement;
    }

    /**
     * 同性质端口分组 <辅助网络，同性质端口列表>
     */
    public Map<String, List<SceneElementPortBO>> getCloudElementGroupPorts(CloudPluginSceneElementBO cloudElement) {
        Map<String, List<SceneElementPortBO>> groupPortsMap = new HashMap<>();
        var srcElement = cloudElement.getSrcElement();
        var srcPorts = srcElement.getPorts();
        if (srcPorts == null) {
            return groupPortsMap;
        }

        for (var srcPort : srcPorts) {
            var portAuxiliaryNetworkCloudElement = getPortAuxiliaryNetworkCloudElement(srcPort.getId());
            if (portAuxiliaryNetworkCloudElement == null) {
                continue;
            }
            var portAuxiliaryNetworkCloudElementNodeId = portAuxiliaryNetworkCloudElement.getSrcElement().getId();
            var groupPorts = groupPortsMap.getOrDefault(portAuxiliaryNetworkCloudElementNodeId, new ArrayList<>());
            groupPorts.add(srcPort);
            groupPortsMap.put(portAuxiliaryNetworkCloudElementNodeId, groupPorts);
        }
        return groupPortsMap;
    }

    /**
     * 获取网络连接的所有端口
     */
    public List<SceneElementPortBO> getAuxiliaryNetworkCloudElementAttachPorts(
            CloudPluginSceneElementBO auxiliaryNetworkCloudElement) {
        var groupPortsMap = getCloudElementGroupPorts(auxiliaryNetworkCloudElement);
        var attachSrcPorts = groupPortsMap.get(auxiliaryNetworkCloudElement.getSrcElement().getId());
        List<SceneElementPortBO> allAttachPorts = new ArrayList<>(attachSrcPorts);
        attachSrcPorts.forEach(attachSrcPort -> {
            var connectedOpaqueSrcPort = getPortConnectedOpaqueSrcPort(attachSrcPort.getId());
            if (connectedOpaqueSrcPort != null) {
                allAttachPorts.add(connectedOpaqueSrcPort);
            }
        });
        return allAttachPorts;
    }

    /**
     * 获得节点连接的不透明元素，忽略透明模式节点
     * @param nodeId 节点id
     * @return 节点连接的不透明元素
     */
    public List<SceneElementBO> getEleConnectedOpaqueSrcElements(String nodeId) {
        Map<String, SceneElementBO> nodeIdSrcElementMap = new HashMap<>();
        List<SceneElementPortBO> connectedOpaqueSrcPorts = getEleConnectedOpaqueSrcPorts(nodeId);
        for (var connectedOpaqueSrcPort : connectedOpaqueSrcPorts) {
            var connectedOpaqueSrcElement = getPluginHandler().getPortSrcElement(connectedOpaqueSrcPort.getId());
            nodeIdSrcElementMap.put(connectedOpaqueSrcElement.getId(), connectedOpaqueSrcElement);
        }

        return new ArrayList<>(nodeIdSrcElementMap.values());
    }

    /**
     * 获得节点连接的不透明端口，忽略透明模式节点
     * @param nodeId 节点id
     * @return 节点连接的不透明端口
     */
    public List<SceneElementPortBO> getEleConnectedOpaqueSrcPorts(String nodeId) {
        List<SceneElementPortBO> connectedOpaqueSrcPorts = new ArrayList<>();
        var srcElement = getPluginHandler().getSrcElement(nodeId);
        var srcPorts = srcElement.getPorts();
        if (srcPorts == null) {
            return connectedOpaqueSrcPorts;
        }

        for (var srcPort : srcPorts) {
            var connectedOpaqueSrcPort = getPortConnectedOpaqueSrcPort(srcPort.getId());
            if (connectedOpaqueSrcPort != null) {
                connectedOpaqueSrcPorts.add(connectedOpaqueSrcPort);
            }
        }

        return connectedOpaqueSrcPorts;
    }

    /**
     * 获得端口连接的不透明端口，忽略透明模式节点
     * @param portId 端口id
     * @return 节点连接的不透明元素
     */
    public SceneElementPortBO getPortConnectedOpaqueSrcPort(String portId) {
        var connectedSrcPort = getPluginHandler().getPortConnectedSrcPort(portId);
        if (connectedSrcPort == null) {
            return null;
        }

        var connectedSrcElement = getPluginHandler().getPortConnectedSrcElement(portId);
        if (connectedSrcElement == null) {
            return null;
        }

        var connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
        if (connectedCloudElement == null || !Boolean.TRUE.equals(connectedCloudElement.getTransparent())) {
            return connectedSrcPort;
        }

        var transparentSrcPorts = connectedSrcElement.getPorts();
        if (transparentSrcPorts == null) {
            return null;
        }

        for (var transparentSrcPort : transparentSrcPorts) {
            if (connectedSrcPort.getId().equals(transparentSrcPort.getId())) {
                continue;
            }
            var subConnectedSrcPort = getPortConnectedOpaqueSrcPort(transparentSrcPort.getId());
            if (subConnectedSrcPort != null) {
                return subConnectedSrcPort;
            }
        }

        return null;
    }

    /**
     * 获得端口连接的不透明节点，忽略透明模式节点
     * @param portId 端口id
     * @return 节点连接的不透明元素
     */
    public SceneElementBO getPortConnectedOpaqueSrcElement(String portId) {
        var connectedSrcPort = getPortConnectedOpaqueSrcPort(portId);
        if (connectedSrcPort == null) {
            return null;
        }

        return getPluginHandler().getPortSrcElement(connectedSrcPort.getId());
    }

}
