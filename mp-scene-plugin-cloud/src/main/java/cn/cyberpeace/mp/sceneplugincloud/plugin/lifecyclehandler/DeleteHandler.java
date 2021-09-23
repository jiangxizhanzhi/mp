package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DeleteHandler extends HandlerCommon {

    private final VirtualSwitch vsClient = new VirtualSwitch();
    private final VirtualRouter vrClient = new VirtualRouter();
    private final VirtualFirewall vfClient = new VirtualFirewall();
    private final VirtualMachine vmClient = new VirtualMachine();
    private final Container containerClient = new Container();
    private final EntityNode enClient = new EntityNode();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ElementExtraCloudInfo {
        private List<String> qosIds;
        private List<String> connectionIds;
        private List<String> proxyIds;
    }

    private ElementExtraCloudInfo collectElementExtraCloudInfo(CloudPluginSceneElementBO cloudElement) {
        List<String> qosIds = new ArrayList<>();
        List<String> connectionIds = new ArrayList<>();
        List<String> proxyIds = new ArrayList<>();
        var srcPorts = cloudElement.getSrcElement().getPorts();
        srcPorts.forEach(srcPort -> {
            var cloudPort = getPluginHandler().getPort(srcPort.getId());
            if (cloudPort == null) {
                return;
            }

            var qosId = cloudPort.getQosId();
            if (!StringUtils.isEmpty(qosId)) {
                qosIds.add(qosId);
            }

            var accessModes = cloudPort.getAccessModes();
            if (accessModes != null) {
                accessModes.forEach(accessMode -> {
                    var connectionId = accessMode.getConnectionId();
                    if (!StringUtils.isEmpty(connectionId)) {
                        connectionIds.add(connectionId);
                    }

                    var proxyId = accessMode.getProxyId();
                    if (!StringUtils.isEmpty(proxyId)) {
                        proxyIds.add(proxyId);
                    }
                });
            }
        });
        return new ElementExtraCloudInfo(qosIds, connectionIds, proxyIds);
    }

    private boolean deleteAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getAuxiliaryNetworkId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new VirtualSwitchDeleteData();
        deleteData.setEntityNetwork(cloudElement.getAuxiliaryNetworkVlan());
        deleteData.setIds(List.of(resourceId));
        return vsClient.delete(deleteData);
    }

    private boolean deleteVirtualRouter(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new VirtualRouterDeleteData();
        deleteData.setIds(List.of(resourceId));
        return vrClient.delete(deleteData);
    }

    private boolean deleteVirtualFirewall(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new VirtualFirewallDeleteData();
        deleteData.setIds(List.of(resourceId));
        return vfClient.delete(deleteData);
    }

    private boolean deleteVirtualMachine(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new VirtualMachineDeleteData();
        deleteData.setVms(List.of(resourceId));
        var extraInfo = collectElementExtraCloudInfo(cloudElement);
        // TODO qos列表
        deleteData.setConnections(extraInfo.getConnectionIds());
        deleteData.setPortMappings(extraInfo.getProxyIds());
        return vmClient.delete(deleteData);
    }

    private boolean deleteContainer(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new ContainerDeleteData();
        deleteData.setContainers(List.of(resourceId));
        var extraInfo = collectElementExtraCloudInfo(cloudElement);
        // TODO qos列表
        deleteData.setConnections(extraInfo.getConnectionIds());
        deleteData.setPortMappings(extraInfo.getProxyIds());
        return containerClient.delete(deleteData);
    }

    private boolean deleteEntityNode(CloudPluginSceneElementBO cloudElement) {
        var deleteData = new EntityNodeDeleteData();
        deleteData.setName(cloudElement.getImage());
        var extraInfo = collectElementExtraCloudInfo(cloudElement);
        deleteData.setConnections(extraInfo.getConnectionIds());
        return enClient.delete(deleteData);
    }

    private boolean deleteElement(CloudPluginSceneElementBO cloudElement) {
        if (Boolean.TRUE.equals(cloudElement.getExternal())) {
            return true;
        }
        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            return true;
        }

        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            return deleteVirtualRouter(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            return deleteVirtualFirewall(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            return deleteVirtualMachine(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            return deleteContainer(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            return deleteEntityNode(cloudElement);
        } else {
            return true;
        }
    }

    public DeleteHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler);
    }

    public void delete() {
        // 删除终端
        for (var cloudElement : getPluginHandler().getTerminalElementMap().values()) {
            deleteElement(cloudElement);
        }

        // 删除防火墙
        for (var cloudElement : getPluginHandler().getFirewallElementMap().values()) {
            deleteElement(cloudElement);
        }

        // 删除网关
        for (var cloudElement : getPluginHandler().getGatewayElementMap().values()) {
            deleteElement(cloudElement);
        }

        // 删除辅助网络(包含虚拟交换机)
        for (var cloudElement : getPluginHandler().getElementMap().values()) {
            if (Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
                deleteAuxiliaryNetwork(cloudElement);
            }
        }
    }
}
