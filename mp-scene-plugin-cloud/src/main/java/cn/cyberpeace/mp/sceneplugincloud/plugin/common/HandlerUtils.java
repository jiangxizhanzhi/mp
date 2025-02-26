 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HandlerUtils.java.
 // File size 25266.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.DnsPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginContext;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.ICloudResourceName;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.RsourceNameParams;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName HandlerUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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

    private List<String> dnsIpAndDomain = null;

    private List<CloudPluginSceneConfigNetworkModel> configNetworks = null ;

    public List<CloudPluginSceneConfigNetworkModel> getConfigNetworks() {
        return configNetworks;
    }

    public void setConfigNetworks(List<CloudPluginSceneConfigNetworkModel> configNetworks) {
        this.configNetworks = configNetworks;
    }

    /**
     * getDnsIpAndDomains
     *
     * @return
     */
    public List<String> getDnsIpAndDomains() {
        if (DnsIpAndDomainHolder.INSTANCE == null) {
            initializeDnsIpAndDomains();
        }
        return DnsIpAndDomainHolder.INSTANCE;
    }

    private static class DnsIpAndDomainHolder {
        private static volatile List<String> INSTANCE;
    }

    private synchronized void initializeDnsIpAndDomains() {
        if (DnsIpAndDomainHolder.INSTANCE == null) {
            List<String> result = new ArrayList<>();
            List<DnsPool> dnsNameservers = getPluginHandler().getScene().getDnsNameservers();

            if (CollectionUtils.isNotEmpty(dnsNameservers)) {
                for (DnsPool dns : dnsNameservers) {
                    CloudPluginSceneElementBO dnsCloudElement = getPluginHandler().getElement(dns.getNodeId());
                    String dnsIp = null;

                    if (dnsCloudElement != null) {
                        SceneElementBO e = dnsCloudElement.getSrcElement();

                        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(e.getPorts())) {
                            for (SceneElementPortBO p : e.getPorts()) {
                                if (StringUtils.isNotEmpty(p.getIp())) {
                                    dnsIp = p.getIp();
                                    break;
                                }
                            }
                        }
                    }
                    result.add((dnsIp != null ? dnsIp : "null") + " " + dns.getDomain());
                }
            }
            DnsIpAndDomainHolder.INSTANCE = result;
        }
    }
    //public List<String> getDnsIpAndDomains() {
    //    if (this.dnsIpAndDomain == null) {
    //        synchronized (this) {
    //            if (this.dnsIpAndDomain == null) {
    //                List<String> result = new ArrayList<>();
    //                if (CollectionUtils.isNotEmpty(getPluginHandler().getScene().getDnsNameservers())) {
    //                    for (DnsPool dns : getPluginHandler().getScene().getDnsNameservers()) {
    //                        CloudPluginSceneElementBO dnsCloudElement = getPluginHandler().getElement(dns.getNodeId());
    //                        String dnsIp = null;
    //                        if (dnsCloudElement != null) {
    //                            SceneElementBO e = dnsCloudElement.getSrcElement();
    //                            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(e.getPorts())) {
    //                                for (SceneElementPortBO p : e.getPorts()) {
    //                                    if (StringUtils.isNotEmpty(p.getIp())) {
    //                                        dnsIp = p.getIp();
    //                                        break;
    //                                    }
    //                                }
    //                            }
    //                        }
    //                        result.add(dnsIp + " " + dns.getDomain());
    //                    }
    //                }
    //                this.dnsIpAndDomain = result;
    //            }
    //        }
    //    }
    //    return this.dnsIpAndDomain;
    //}

    /**
     * addFwdnsInterfaceIp
     *
     * @param node
     * @param netId
     * @param fwdnsInterfaceIp
     */
    public void addFwdnsInterfaceIp(String node, String netId, String fwdnsInterfaceIp) {
        getPluginHandler().getElement(node).getFwdnsIps().putIfAbsent(netId, fwdnsInterfaceIp);
    }

    /**
     * getFwdnsInterfaceIp
     *
     * @param node
     * @param netId
     * @return
     */
    public String getFwdnsInterfaceIp(String node, String netId) {
        return getPluginHandler().getElement(node).getFwdnsIps().get(netId);
    }

    /**
     * addManualConfigIpPort
     *
     * @param port
     */
    public void addManualConfigIpPort(SceneElementPortBO port) {
        if (port != null && port.getId() != null) {
            this.manualConfigIpPort.add(port.getId());
        }
    }

    /**
     * isManualConfigIpPort
     *
     * @param port
     * @return boolean
     */
    public boolean isManualConfigIpPort(SceneElementPortBO port) {
        if (port != null && port.getId() != null) {
            return this.manualConfigIpPort.contains(port.getId());
        }
        return false;
    }

    /**
     * getNetworkByPort
     *
     * @param port
     * @return
     */
    public CloudPluginSceneElementNetworkBO getNetworkByPort(SceneElementPortBO port) {
        return this.portId2network.get(port.getId());
    }

    /**
     * addNetwork
     *
     * @param port
     * @param network
     */
    public void addNetwork(SceneElementPortBO port, CloudPluginSceneElementNetworkBO network) {
        this.portId2network.put(port.getId(), network);
    }

    /**
     * setPortNetworks
     *
     * @param portId2network
     */
    public void setPortNetworks(Map<String, CloudPluginSceneElementNetworkBO> portId2network) {
        portId2network.forEach((k, v) -> {
            this.portId2network.put(k, v);
        });
    }

    /**
     * HandlerUtils
     *
     * @param pluginHandler
     */
    public HandlerUtils(CloudScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
    }

    /**
     * isExternalNetwork
     *
     * @param networkId
     * @return boolean
     */
    public static boolean isExternalNetwork(String networkId) {
        if (StringUtils.isEmpty(networkId)) {
            return false;
        }
        return networkId.equals(CloudSingleton.cloudAppConfig.getExternalNetId());
    }

    /**
     * genResourceName
     *
     * @param name
     * @return
     */
    public static String genResourceName(String name) {
        return BeanCommon.getBean(ICloudResourceName.class).generate(name, null);
    }

    /**
     * genResourceName
     *
     * @param name
     * @param index
     * @return
     */
    public static String genResourceName(String name, int index) {
        RsourceNameParams params = new RsourceNameParams();
        params.setIndex(index);
        return BeanCommon.getBean(ICloudResourceName.class).generate(name, params);
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

        SceneElementBO srcElement = cloudElement.getSrcElement();
        SceneElementBO connectedElement = connectedCloudElement.getSrcElement();
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
     * getRequestUser
     *
     * @return
     */
    public String getRequestUser() {
        CloudScenePluginContext context = getPluginHandler().getContext();
        if (context == null) {
            return null;
        }

        return context.getRequestUser();
    }

    public String getRequestUserOrg() {
        CloudScenePluginContext context = getPluginHandler().getContext();
        if (context == null) {
            return null;
        }

        return context.getRequestUserOrg();
    }

    /**
     * randomCidrs
     *
     * @param count
     * @param existCidrs
     * @return
     */
    public List<String> randomCidrs(int count, List<String> existCidrs) {
        return IpUtils.randomCidrs(cidrPool, count, existCidrs);
    }

    /**
     * statExistCidrs
     *
     * @return
     */
    public List<String> statExistCidrs() {
        List<String> existCidrs = new ArrayList<>();
        getPluginHandler().getElementMap().forEach((nodeId, element) -> {
            String cidr = element.getCidr();
            if (!StringUtils.isEmpty(cidr)) {
                existCidrs.add(cidr);
            }

            String auxiliaryNetworkCidr = element.getAuxiliaryNetworkCidr();
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
            CloudPluginSceneElementBO auxiliaryNetworkCloudElement = getPortAuxiliaryNetworkCloudElement(portId);
            if (auxiliaryNetworkCloudElement != null) {
                String nodeId = auxiliaryNetworkCloudElement.getSrcElement().getId();
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

    /**
     * addOuterExistIps
     *
     * @param auxiliaryNetworkCloudElement
     * @param outerExistIps
     */
    public void addOuterExistIps(CloudPluginSceneElementBO auxiliaryNetworkCloudElement, List<String> outerExistIps) {
        String nodeId = auxiliaryNetworkCloudElement.getSrcElement().getId();
        MapUtils.setDefault(networkNodeIdOuterExistIpsMap, nodeId, new HashSet<>()).addAll(outerExistIps);
    }

    /**
     * getOuterExistIps
     *
     * @param auxiliaryNetworkCloudElement
     * @return
     */
    public Set<String> getOuterExistIps(CloudPluginSceneElementBO auxiliaryNetworkCloudElement) {
        String nodeId = auxiliaryNetworkCloudElement.getSrcElement().getId();
        return networkNodeIdOuterExistIpsMap.getOrDefault(nodeId, new HashSet<>());
    }

    /**
     * 获取端口所在辅助网络网元
     * @param portId 网元端口id
     * @return 端口所在辅助网络网元
     */
    public CloudPluginSceneElementBO getPortAuxiliaryNetworkCloudElement(String portId) {
        if (portId == null) {
            return null;
        }

        if (portIdAuxiliaryNetworkCloudElementMap.containsKey(portId)) {
            return portIdAuxiliaryNetworkCloudElementMap.get(portId);
        }
        SceneElementBO srcElement = getPluginHandler().getSrcPort(portId).getElement();
        CloudPluginSceneElementBO cloudElement = getPluginHandler().getElement(srcElement.getId());
        SceneElementPortBO connectedOpaqueSrcPort = getPortConnectedOpaqueSrcPort(portId);
        if (connectedOpaqueSrcPort == null) {
            // 未连接端口情况，如果节点存在其它连接，则忽略该端口
            if (getPluginHandler().getSceneHandler().elementHasConnectedPort(srcElement.getId())) {
                return null;
            }
        }

        SceneElementBO connectedOpaqueSrcElement = connectedOpaqueSrcPort != null ? getPluginHandler().getPortSrcElement(connectedOpaqueSrcPort.getId()) : null;
        CloudPluginSceneElementBO connectedCloudElement = connectedOpaqueSrcElement != null ? getPluginHandler().getElement(connectedOpaqueSrcElement.getId()) : null;
        CloudPluginSceneElementBO auxiliaryNetworkCloudElement = confirmAuxiliaryNetworkCloudElement(cloudElement, connectedCloudElement);
        if (auxiliaryNetworkCloudElement == null) {
            return null;
        }

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
        SceneElementBO srcElement = cloudElement.getSrcElement();
        List<SceneElementPortBO> srcPorts = srcElement.getPorts();
        if (srcPorts == null) {
            return groupPortsMap;
        }

        for (SceneElementPortBO srcPort : srcPorts) {
            CloudPluginSceneElementBO portAuxiliaryNetworkCloudElement = getPortAuxiliaryNetworkCloudElement(srcPort.getId());
            if (portAuxiliaryNetworkCloudElement == null) {
                continue;
            }
            String portAuxiliaryNetworkCloudElementNodeId = portAuxiliaryNetworkCloudElement.getSrcElement().getId();
            List<SceneElementPortBO> groupPorts = groupPortsMap.getOrDefault(portAuxiliaryNetworkCloudElementNodeId, new ArrayList<>());
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
        Map<String, List<SceneElementPortBO>> groupPortsMap = getCloudElementGroupPorts(auxiliaryNetworkCloudElement);
        List<SceneElementPortBO> attachSrcPorts = groupPortsMap.get(auxiliaryNetworkCloudElement.getSrcElement().getId());
        List<SceneElementPortBO> allAttachPorts = new ArrayList<>(attachSrcPorts);
        attachSrcPorts.forEach(attachSrcPort -> {
            SceneElementPortBO connectedOpaqueSrcPort = getPortConnectedOpaqueSrcPort(attachSrcPort.getId());
            if (connectedOpaqueSrcPort != null) {
                allAttachPorts.add(connectedOpaqueSrcPort);
            }
        });
        return allAttachPorts;
    }

    /**
     * @deprecated 替代方法 cn.cyberpeace.mp.scene.core.plugin.common.BasePluginCommonHandler#getElementConnectedSrcElements(java.lang.String)
     * 获得节点连接的不透明元素，忽略透明模式节点
     * @param nodeId 节点id
     * @return 节点连接的不透明元素
     */
    @Deprecated
    public List<SceneElementBO> getEleConnectedOpaqueSrcElements(String nodeId) {
        Map<String, SceneElementBO> nodeIdSrcElementMap = new HashMap<>();
        List<SceneElementPortBO> connectedOpaqueSrcPorts = getEleConnectedOpaqueSrcPorts(nodeId);
        for (SceneElementPortBO connectedOpaqueSrcPort : connectedOpaqueSrcPorts) {
            SceneElementBO connectedOpaqueSrcElement = getPluginHandler().getPortSrcElement(connectedOpaqueSrcPort.getId());
            nodeIdSrcElementMap.put(connectedOpaqueSrcElement.getId(), connectedOpaqueSrcElement);
        }

        return new ArrayList<>(nodeIdSrcElementMap.values());
    }

    /**
     * @deprecated 替代方法 cn.cyberpeace.mp.scene.core.plugin.common.BasePluginCommonHandler#getElementConnectedSrcPorts(java.lang.String)
     * 获得节点连接的不透明端口，忽略透明模式节点
     * @param nodeId 节点id
     * @return 节点连接的不透明端口
     */
    @Deprecated
    public List<SceneElementPortBO> getEleConnectedOpaqueSrcPorts(String nodeId) {
        List<SceneElementPortBO> connectedOpaqueSrcPorts = new ArrayList<>();
        SceneElementBO srcElement = getPluginHandler().getSrcElement(nodeId);
        List<SceneElementPortBO> srcPorts = srcElement.getPorts();
        if (srcPorts == null) {
            return connectedOpaqueSrcPorts;
        }

        for (SceneElementPortBO srcPort : srcPorts) {
            SceneElementPortBO connectedOpaqueSrcPort = getPortConnectedOpaqueSrcPort(srcPort.getId());
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
     * @deprecated 替代方法为 cn.cyberpeace.mp.scene.core.plugin.common.BasePluginCommonHandler#getPortConnectedSrcPort(java.lang.String)
     */
    @Deprecated
    public SceneElementPortBO getPortConnectedOpaqueSrcPort(String portId) {
        SceneElementPortBO connectedSrcPort = getPluginHandler().getPortConnectedSrcPort(portId);
        if (connectedSrcPort == null) {
            return null;
        }

        SceneElementBO connectedSrcElement = getPluginHandler().getPortConnectedSrcElement(portId);
        if (connectedSrcElement == null) {
            return null;
        }

        CloudPluginSceneElementBO connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
        if (connectedCloudElement == null || !Boolean.TRUE.equals(connectedCloudElement.getTransparent())) {
            return connectedSrcPort;
        }

        List<SceneElementPortBO> transparentSrcPorts = connectedSrcElement.getPorts();
        if (transparentSrcPorts == null) {
            return null;
        }

        for (SceneElementPortBO transparentSrcPort : transparentSrcPorts) {
            if (connectedSrcPort.getId().equals(transparentSrcPort.getId())) {
                continue;
            }
            SceneElementPortBO subConnectedSrcPort = getPortConnectedOpaqueSrcPort(transparentSrcPort.getId());
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
     * @deprecated 替代方法为 cn.cyberpeace.mp.scene.core.plugin.common.BasePluginCommonHandler#getPortSrcElement(java.lang.String)
     */
    @Deprecated
    public SceneElementBO getPortConnectedOpaqueSrcElement(String portId) {
        SceneElementPortBO connectedSrcPort = getPortConnectedOpaqueSrcPort(portId);
        if (connectedSrcPort == null) {
            return null;
        }

        return getPluginHandler().getPortSrcElement(connectedSrcPort.getId());
    }

    public boolean isAddedPort(String portId, CloudScenePluginHandler newHandler, CloudScenePluginHandler originHandler) {
        Set<String> newPorts = newHandler.getElementPortMap().keySet();
        Set<String> originPorts = originHandler.getElementPortMap().keySet();
        if (newPorts.contains(portId) && !originPorts.contains(portId)) {
            return true;
        } else {
            return false;
        }
    }

}
