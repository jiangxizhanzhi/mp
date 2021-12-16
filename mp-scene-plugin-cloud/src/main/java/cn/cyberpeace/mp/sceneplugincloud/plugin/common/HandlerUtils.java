package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class HandlerUtils<H extends ScenePluginHandler<? extends BaseCloudPluginSceneBO<?, ?, ?>,
        ? extends BaseCloudPluginSceneElementBO<?, ?, ?>, ? extends BaseCloudPluginSceneElementPortBO<?, ?, ?>, ?>> {

    private H pluginHandler;

    private final List<String> cidrPool = CloudSingleton.cloudAppConfig.getCidrPool();

    public HandlerUtils(H pluginHandler) {
        this.pluginHandler = pluginHandler;
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
     * 确认连接使用的辅助网络所在节点
     * @param cloudElement 源节点
     * @param connectedCloudElement 连接节点
     * @return 辅助网络所在节点
     */
    public CloudPluginSceneElementBO confirmAuxiliaryNetworkCloudElement(
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

    public boolean isExternalNetwork(String networkId) {
        if (StringUtils.isEmpty(networkId)) {
            return false;
        }
        return networkId.equals(CloudSingleton.cloudAppConfig.getExternalNetId());
    }
}
