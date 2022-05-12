package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;

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
}
