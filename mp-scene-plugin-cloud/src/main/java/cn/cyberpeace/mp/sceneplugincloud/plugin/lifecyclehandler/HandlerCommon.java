package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class HandlerCommon {

    private final List<String> cidrPool = CloudSingleton.cloudAppConfig.getCidrPool();

    private final CloudScenePluginHandler pluginHandler;

    public HandlerCommon(CloudScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
    }

    public List<String> randomCidrs(int count, List<String> existCidrs) {
        return IpUtils.randomCidrs(cidrPool, count, existCidrs);
    }

    public List<String> statExistCidrs() {
        List<String> existCidrs = new ArrayList<>();
        pluginHandler.getElementMap().forEach((nodeId, element) -> {
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

    public String genResourceName(String name) {
        return StringUtils.joinWith("_", CloudSingleton.cloudAppConfig.getCpcloudResourceGroup(), name);
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

        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return cloudElement;
        }

        Integer connectedVirtualType = connectedCloudElement.getVirtualType();
        if (connectedVirtualType != null
                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return connectedCloudElement;
        }

        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return cloudElement;
        }

        if (connectedVirtualType != null
                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return connectedCloudElement;
        }

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
            var connectedOpaqueSrcElement = pluginHandler.getPortSrcElement(connectedOpaqueSrcPort.getId());
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
        var srcElement = pluginHandler.getSrcElement(nodeId);
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
        var connectedSrcPort = pluginHandler.getPortConnectedSrcPort(portId);
        if (connectedSrcPort == null) {
            return null;
        }

        var connectedSrcElement = pluginHandler.getPortConnectedSrcElement(portId);
        if (connectedSrcElement == null) {
            return null;
        }

        var connectedCloudElement = pluginHandler.getElement(connectedSrcElement.getId());
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

        return pluginHandler.getPortSrcElement(connectedSrcPort.getId());
    }

}
