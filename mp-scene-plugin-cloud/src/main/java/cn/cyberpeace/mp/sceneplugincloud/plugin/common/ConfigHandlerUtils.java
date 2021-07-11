package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import lombok.Getter;


@Getter
public class ConfigHandlerUtils {
    private CloudSceneConfigPluginHandler pluginHandler;

    public ConfigHandlerUtils(CloudSceneConfigPluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
    }

    
    public CloudPluginSceneConfigElementBO getPortAuxiliaryNetworkCloudElement(String portId) {
        if (portId == null) {
            return null;
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
        return auxiliaryNetworkCloudElement;
    }

    public SceneConfigElementPortBO getPortConnectedOpaqueSrcPort(String portId) {
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

    public static CloudPluginSceneConfigElementBO confirmAuxiliaryNetworkCloudElement(
            CloudPluginSceneConfigElementBO cloudElement, CloudPluginSceneConfigElementBO connectedCloudElement) {
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

        
        if (type != null && type.equals(BaseSceneElementModel.Type.SWITCH)) {
            return cloudElement;
        }
        if (connectedType != null && connectedType.equals(BaseSceneElementModel.Type.SWITCH)) {
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
}
