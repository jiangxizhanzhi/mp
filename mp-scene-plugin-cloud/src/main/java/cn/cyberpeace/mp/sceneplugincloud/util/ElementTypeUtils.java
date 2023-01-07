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

    public static boolean isNs3(CloudPluginSceneElementBO element) {
        return element.getVirtualType() != null && element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.NPC
                && "64ea7191-717c-4bad-8ddb-a01ae145e85b".equals(element.getImageUuid());
    }

    public static boolean isNpc(CloudPluginSceneElementBO element) {
        return element.getVirtualType() != null && element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.NPC;
    }

    public static boolean isVirtualMachine(CloudPluginSceneElementBO element) {
        return element.getVirtualType() != null && element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE;
    }

    public static boolean isContainer(CloudPluginSceneElementBO element) {
        return element.getVirtualType() != null && element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER;
    }

    public static boolean isVirtualTerminal(CloudPluginSceneElementBO element) {
        return isNs3(element) || isNpc(element) || isContainer(element) || isVirtualMachine(element);
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
}
