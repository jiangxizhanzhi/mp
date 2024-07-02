package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;

/**
 * @Description 用于节点类型的判断
 * @Author maojun
 * @Date 2022/4/28
 */
public class ElementTypeUtils {

    private static String CORE_ROUTER_IMAGE_NAME = "Core Router";

    /**
     * 是否核心路由器
     * @param image
     * @return
     */
    public static boolean isCoreRouter(String image) {
        return CORE_ROUTER_IMAGE_NAME.equals(image);
    }

    /**
     * 是否核心路由器
     * @param element
     * @return
     */
    public static boolean isCoreRouter(CloudPluginSceneElementBO element) {
        return isCoreRouter(element.getImage());
    }

    /**
     * isExternal
     *
     * @param element
     * @return boolean
     */
    public static boolean isExternal(CloudPluginSceneElementBO element) {
        return Boolean.TRUE.equals(element.getExternal());
    }

    /**
     * isExternalTerminal
     *
     * @param cloudElement
     * @return boolean
     */
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

    /**
     * isEntityGateway
     *
     * @param cloudElement
     * @return boolean
     */
    public static boolean isEntityGateway(CloudPluginSceneElementBO cloudElement) {
        String entityType = BaseCloudPluginSceneElementModel.ImageType.ENTITY;
        int routerRole = BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER;
        int switchRole = BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH;
        int firewallRole = BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL;
        Integer role = cloudElement.getRole();
        if (entityType.equals(cloudElement.getImageType()) && role != null && (role == routerRole || role == switchRole || role == firewallRole)) {
            return true;
        }
        return false;
    }

    /**
     * isVirtualGateway
     *
     * @param element
     * @return boolean
     */
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

    /**
     * isVirtualNode
     *
     * @param element
     * @return boolean
     */
    public static boolean isVirtualNode(CloudPluginSceneElementBO element) {
        Integer virtualType = element.getVirtualType();
        boolean isVirtualElement = virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS);
        return isVirtualElement;
    }
}
