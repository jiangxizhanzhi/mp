package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DeleteHandler extends HandlerCommon {

    private final VirtualSwitch vsClient = new VirtualSwitch();
    private final VirtualRouter vrClient = new VirtualRouter();
    private final VirtualFirewall vfClient = new VirtualFirewall();
    private final VirtualMachine vmClient = new VirtualMachine();
    private final Container containerClient = new Container();
    private final EntityNode enClient = new EntityNode();

    private void deleteAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getAuxiliaryNetworkId();
        if (StringUtils.isEmpty(resourceId)) {
            return;
        }

        var deleteData = new VirtualSwitchDeleteData();
        deleteData.setEntityNetwork(cloudElement.getAuxiliaryNetworkVlan());
        deleteData.setIds(List.of(resourceId));
        vsClient.delete(deleteData);
    }

    private void deleteVirtualRouter(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return;
        }

        var deleteData = new VirtualRouterDeleteData();
        deleteData.setIds(List.of(resourceId));
        vrClient.delete(deleteData);
    }

    private void deleteVirtualFirewall(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return;
        }

        var deleteData = new VirtualFirewallDeleteData();
        deleteData.setIds(List.of(resourceId));
        vfClient.delete(deleteData);
    }

    private void deleteVirtualMachine(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return;
        }

        var deleteData = new VirtualMachineDeleteData();
        deleteData.setVms(List.of(resourceId));
        vmClient.delete(deleteData);
    }

    private void deleteContainer(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return;
        }

        var deleteData = new ContainerDeleteData();
        deleteData.setContainers(List.of(resourceId));
        containerClient.delete(deleteData);
    }

    private void deleteEntityNode(CloudPluginSceneElementBO cloudElement) {
        var deleteData = new EntityNodeDeleteData();
        deleteData.setName(cloudElement.getImage());
        enClient.delete(deleteData);
    }

    private void deleteElement(CloudPluginSceneElementBO cloudElement) {
        if (Boolean.TRUE.equals(cloudElement.getExternal())) {
            return;
        }
        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            return;
        }

        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            deleteVirtualRouter(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            deleteVirtualFirewall(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            deleteVirtualMachine(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            deleteContainer(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            deleteEntityNode(cloudElement);
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
