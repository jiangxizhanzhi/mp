package cn.cyberpeace.mp.sceneplugincloud.util;

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

    public static boolean isExternal(CloudPluginSceneElementBO element) {
        return Boolean.TRUE.equals(element.getExternal());
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
}
