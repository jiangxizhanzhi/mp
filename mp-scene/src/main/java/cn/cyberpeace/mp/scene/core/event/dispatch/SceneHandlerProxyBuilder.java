package cn.cyberpeace.mp.scene.core.event.dispatch;

import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import org.springframework.cglib.proxy.Enhancer;

/**
 * @Description
 * @Author maojun
 * @Date 2022/4/18
 */
public final class SceneHandlerProxyBuilder {
    public static SceneHandler build(String id, String areaId, boolean isAreaMode, SceneContextBO context, boolean realTimeUpdate) {
        SceneHandler sceneHandler = new SceneHandler(id, areaId, isAreaMode, context);
        sceneHandler.setRealTimeUpdate(realTimeUpdate);
        return build(sceneHandler);
    }

    public static SceneHandler build(SceneHandler sceneHandler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SceneHandler.class);
        enhancer.setCallback(new SceneHandlerProxy(sceneHandler));
        return (SceneHandler) enhancer.create();
    }
}
