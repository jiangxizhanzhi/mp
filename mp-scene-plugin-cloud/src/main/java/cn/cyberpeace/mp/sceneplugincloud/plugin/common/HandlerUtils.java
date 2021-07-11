package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.ICloudResourceName;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.RsourceNameParams;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
public class HandlerUtils {

    private CloudScenePluginHandler pluginHandler;

    private final List<String> cidrPool = CloudSingleton.cloudAppConfig.getCidrPool();

    private final Map<String, CloudPluginSceneElementBO> portIdAuxiliaryNetworkCloudElementMap = new ConcurrentHashMap<>();
    private final Map<String, List<String>> networkNodeIdPortIdsMap = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> networkNodeIdOuterExistIpsMap = new ConcurrentHashMap<>();

    private final Map<String, CloudPluginSceneElementNetworkBO> portId2network = new ConcurrentHashMap<>();

    private final Set<String> manualConfigIpPort = new HashSet<>();

    private Set<String> createdNetworks = new HashSet<>();

    private Set<String> toDeleteNodes = new HashSet<>();

    public void addFwdnsInterfaceIp(String node, String netId, String fwdnsInterfaceIp) {
        getPluginHandler().getElement(node).getFwdnsIps().putIfAbsent(netId, fwdnsInterfaceIp);
    }

    public String getFwdnsInterfaceIp(String node, String netId) {
        return getPluginHandler().getElement(node).getFwdnsIps().get(netId);
    }

    public void addManualConfigIpPort(SceneElementPortBO port) {
        if (port != null && port.getId() != null) {
            this.manualConfigIpPort.add(port.getId());
        }
    }

    public boolean isManualConfigIpPort(SceneElementPortBO port) {
        if (port != null && port.getId() != null) {
            return this.manualConfigIpPort.contains(port.getId());
        }
        return false;
    }

    public CloudPluginSceneElementNetworkBO getNetworkByPort(SceneElementPortBO port) {
        return this.portId2network.get(port.getId());
    }

    public void addNetwork(SceneElementPortBO port, CloudPluginSceneElementNetworkBO network) {
        this.portId2network.put(port.getId(), network);
    }

    public void setPortNetworks(Map<String, CloudPluginSceneElementNetworkBO> portId2network) {
        portId2network.forEach((k, v) -> {
            this.portId2network.put(k, v);
        });
    }

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
        return BeanCommon.getBean(ICloudResourceName.class).generate(name, null);
    }

    public static String genResourceName(String name, int index) {
        RsourceNameParams params = new RsourceNameParams();
        params.setIndex(index);
        return BeanCommon.getBean(ICloudResourceName.class).generate(name, params);
    }

    
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

        
        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return cloudElement;
        }
        if (connectedVirtualType != null
                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return connectedCloudElement;
        }

        
        if (type != null && type.equals(BaseSceneElementModel.Type.SWITCH)
                && Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return cloudElement;
        }
        if (connectedType != null && connectedType.equals(BaseSceneElementModel.Type.SWITCH)
                && Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
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

        
        if (type != null && type.equals(BaseSceneElementModel.Type.ROUTER)
                && Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return cloudElement;
        }
        if (connectedType != null && connectedType.equals(BaseSceneElementModel.Type.ROUTER)
                && Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return connectedCloudElement;
        }

        
        if (type != null && type.equals(BaseSceneElementModel.Type.FIREWALL)
                && Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return cloudElement;
        }
        if (connectedType != null && connectedType.equals(BaseSceneElementModel.Type.FIREWALL)
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

    
    public void loadAuxiliaryNetworkCloudElementPortIdsMap() {
        getPluginHandler().getSceneHandler().getElementPortMap().forEach((portId, srcPort) -> {
            var auxiliaryNetworkCloudElement = getPortAuxiliaryNetworkCloudElement(portId);
            if (auxiliaryNetworkCloudElement != null) {
                var nodeId = auxiliaryNetworkCloudElement.getSrcElement().getId();
                MapUtils.setDefault(networkNodeIdPortIdsMap, nodeId, new ArrayList<>()).add(portId);
            }
        });
    }

    
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

    
    public CloudPluginSceneElementBO getPortAuxiliaryNetworkCloudElement(String portId) {
        if (portId == null) {
            return null;
        }

        if (portIdAuxiliaryNetworkCloudElementMap.containsKey(portId)) {
            return portIdAuxiliaryNetworkCloudElementMap.get(portId);
        }
        var srcElement = getPluginHandler().getSrcPort(portId).getElement();
        var cloudElement = getPluginHandler().getElement(srcElement.getId());
        var connectedOpaqueSrcPort = getPortConnectedOpaqueSrcPort(portId);
        if (connectedOpaqueSrcPort == null) {
            
            if (getPluginHandler().getSceneHandler().elementHasConnectedPort(srcElement.getId())) {
                return null;
            }
        }

        var connectedOpaqueSrcElement = connectedOpaqueSrcPort != null ? getPluginHandler().getPortSrcElement(connectedOpaqueSrcPort.getId()) : null;
        var connectedCloudElement = connectedOpaqueSrcElement != null ? getPluginHandler().getElement(connectedOpaqueSrcElement.getId()) : null;
        var auxiliaryNetworkCloudElement = confirmAuxiliaryNetworkCloudElement(cloudElement, connectedCloudElement);
        if (auxiliaryNetworkCloudElement == null) {
            return null;
        }

        portIdAuxiliaryNetworkCloudElementMap.put(portId, auxiliaryNetworkCloudElement);
        if (connectedOpaqueSrcPort != null) {
            portIdAuxiliaryNetworkCloudElementMap.put(connectedOpaqueSrcPort.getId(), auxiliaryNetworkCloudElement);
        }
        return auxiliaryNetworkCloudElement;
    }

    
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

    
    public List<SceneElementBO> getEleConnectedOpaqueSrcElements(String nodeId) {
        Map<String, SceneElementBO> nodeIdSrcElementMap = new HashMap<>();
        List<SceneElementPortBO> connectedOpaqueSrcPorts = getEleConnectedOpaqueSrcPorts(nodeId);
        for (var connectedOpaqueSrcPort : connectedOpaqueSrcPorts) {
            var connectedOpaqueSrcElement = getPluginHandler().getPortSrcElement(connectedOpaqueSrcPort.getId());
            nodeIdSrcElementMap.put(connectedOpaqueSrcElement.getId(), connectedOpaqueSrcElement);
        }

        return new ArrayList<>(nodeIdSrcElementMap.values());
    }

    
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

    
    public SceneElementBO getPortConnectedOpaqueSrcElement(String portId) {
        var connectedSrcPort = getPortConnectedOpaqueSrcPort(portId);
        if (connectedSrcPort == null) {
            return null;
        }

        return getPluginHandler().getPortSrcElement(connectedSrcPort.getId());
    }

    public boolean isAddedPort(String portId, CloudScenePluginHandler newHandler, CloudScenePluginHandler originHandler) {
        var newPorts = newHandler.getElementPortMap().keySet();
        var originPorts = originHandler.getElementPortMap().keySet();
        if (newPorts.contains(portId) && !originPorts.contains(portId)) {
            return true;
        } else {
            return false;
        }
    }

}
