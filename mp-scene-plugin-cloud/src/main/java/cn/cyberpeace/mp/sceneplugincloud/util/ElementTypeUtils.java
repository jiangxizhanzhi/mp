package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;


public class ElementTypeUtils {

    private static String CORE_ROUTER_IMAGE_NAME = "Core Router";

    
    public static boolean isCoreRouter(String image) {
        return CORE_ROUTER_IMAGE_NAME.equals(image);
    }

    
    public static boolean isCoreRouter(CloudPluginSceneElementBO element) {
        return isCoreRouter(element.getImage());
    }

    public static boolean isExternal(CloudPluginSceneElementBO element) {
        return Boolean.TRUE.equals(element.getExternal());
    }

    public static boolean isVirtualRouter(CloudPluginSceneElementBO element) {
        return BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER == element.getRole() && BaseCloudPluginSceneElementModel.ImageType.VM.equals(element.getImageType());
    }

    public static boolean isExternalTerminal(CloudPluginSceneElementBO cloudElement) {
        if (!Boolean.TRUE.equals(cloudElement.getExternal())) {
            return false;
        }
        Integer role = cloudElement.getRole();
        if (role != null && role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEntityGateway(CloudPluginSceneElementBO cloudElement) {
        var entityType = BaseCloudPluginSceneElementModel.ImageType.ENTITY;
        var routerRole = BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER;
        var switchRole = BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH;
        var firewallRole = BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL;
        var role = cloudElement.getRole();
        if (entityType.equals(cloudElement.getImageType()) && role != null && (role == routerRole || role == switchRole || role == firewallRole)) {
            return true;
        }
        return false;
    }

    public static boolean isVirtualGateway(CloudPluginSceneElementBO element) {
        if (element == null) {
            return false;
        }
        Integer type = 0;
        if (element.getSrcElement() != null && element.getSrcElement().getType() != null) {
            type = element.getSrcElement().getType();
        }
        Integer virtualType = 0;
        if (element.getVirtualType() != null) {
            virtualType = element.getVirtualType();
        }
        boolean isVirtualGateWayNode = virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH
                || virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER
                || type == BaseSceneElementModel.Type.ROUTER;
        return isVirtualGateWayNode;
    }

    public static boolean isVirtualNode(CloudPluginSceneElementBO element) {
        Integer virtualType = element.getVirtualType();
        var isVirtualElement = virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS);
        return isVirtualElement;
    }
}
