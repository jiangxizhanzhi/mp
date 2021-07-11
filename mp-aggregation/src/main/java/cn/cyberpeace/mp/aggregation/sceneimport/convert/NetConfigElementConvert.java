package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.ExtNodeInfo;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;


class NetConfigElementConvert implements IConverter {

    public void convert2BO(ConvertDataContainer container) {
        CsceneBO csceneBO = container.toCsceneBO();
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(csceneBO.getSceneConfig(), null);
        CloudSceneConfigPluginHandler pluginHandler = sceneConfigHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        
        for (var element : container.getAllElements()) {
            var cloudElement = container.getCloudPluginElements().get(element.getId());
            if (cloudElement == null) {
                continue;
            }
            if (CollectionUtils.isNotEmpty(cloudElement.getStaticRoutes())) {
                for (var sr : cloudElement.getStaticRoutes()) {
                    
                    if (StringUtils.isNotEmpty(sr.getNexthopPortId())) {
                        String realNexthopPortId = null;
                        if (CollectionUtils.isNotEmpty(element.getPorts())) {
                            for (var p : element.getPorts()) {
                                var connectedElement = sceneConfigHandler.getPortConnectedElement(p.getId());
                                if (connectedElement != null && connectedElement.getId().equals(sr.getNexthopPortId())) {
                                    realNexthopPortId = p.getId();
                                    break;
                                }
                            }
                            sr.setNexthopPortId(realNexthopPortId);
                        }
                    }
                }
            }
            if (container.getExtNodeInfos().containsKey(element.getId())) {
                ExtNodeInfo extNodeInfo = container.getExtNodeInfos().get(element.getId());
                cloudElement.setOspfRouterId(extNodeInfo.getOspfRouterId());
                cloudElement.setRipLoopback(extNodeInfo.getRipLoopback());
                if (CollectionUtils.isNotEmpty(extNodeInfo.getPortProtocols())) {
                    Map<String, ExtNodeInfo.PortProtocolExportInfo> nodeId2Protocol = new HashMap<>();
                    Map<String, ExtNodeInfo.PortProtocolExportInfo> portId2Protocol = new HashMap<>();
                    for (var ps : extNodeInfo.getPortProtocols()) {
                        nodeId2Protocol.put(ps.getConnectNode(), ps);
                    }
                    if (CollectionUtils.isNotEmpty(element.getPorts())) {
                        for (var port : element.getPorts()) {
                            var connectElement = sceneConfigHandler.getPortConnectedElement(port.getId());
                            if (connectElement != null) {
                                var portProtocol = nodeId2Protocol.get(connectElement.getId());
                                if (portProtocol != null && container.getCloudPluginPorts().get(port.getId()) != null) {
                                    var cloudPort = container.getCloudPluginPorts().get(port.getId());
                                    port.setDelay(portProtocol.getDelay());
                                    port.setLoss(portProtocol.getLoss());
                                    port.setEgress(portProtocol.getEgress());
                                    port.setIngress(portProtocol.getIngress());
                                    cloudPort.setConnectMode(portProtocol.getConnectMode());
                                    cloudPort.setBgpAs(portProtocol.getBgpAs());
                                    cloudPort.setBgpIp(portProtocol.getBgpIp());
                                    cloudPort.setBgpPeer(portProtocol.getBgpPeer());
                                    cloudPort.setBgpPeerAs(portProtocol.getBgpPeerAs());
                                    cloudPort.setRipIp(portProtocol.getRipIp());
                                    cloudPort.setRipPear(portProtocol.getRipPear());
                                    cloudPort.setOspfArea(portProtocol.getOspfArea());
                                    cloudPort.setOspfIp(portProtocol.getOspfIp());
                                    portId2Protocol.put(port.getId(), portProtocol);
                                }
                            }
                        }
                        container.getPortId2Protocol().putAll(portId2Protocol);
                    }
                }
            }
        }
    }

}
