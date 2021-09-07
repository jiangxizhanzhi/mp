package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
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

        return getPluginHandler().getPortSrcElement(connectedSrcPort.getId());
    }

}
