package cn.cyberpeace.mp.scene.core.event.dispatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 场景事件调度的入口类
 * @Author maojun
 * @Date 2022/5/11
 */
public class SceneEventControllerUtils {
    private static Object lock = new Object();

    private static Map<String, SceneEventControl> sceneEventControllers = new ConcurrentHashMap<>();

    public static boolean acquireEventLock(String sceneId, int event, int eventStatus) {
        if (!sceneEventControllers.containsKey(sceneId)) {
            synchronized (lock) {
                if (!sceneEventControllers.containsKey(sceneId)) {
                    sceneEventControllers.put(sceneId, buildSceneEventControl(sceneId, event, eventStatus));
                }
            }
        }
        return sceneEventControllers.get(sceneId).acquireEventLock(event, eventStatus);
    }

    public static void releaseEventLock(String sceneId) {
        var scene = sceneEventControllers.get(sceneId);
        // todo 极端并发下可能有问题
        if (scene != null && scene.releaseEventLock()) {
            sceneEventControllers.remove(sceneId);
        }
    }

    public static Integer getLatestEvent(String sceneId) {
        var scene = sceneEventControllers.get(sceneId);
        if (scene != null) {
            return scene.getLatestEvent();
        }
        return null;
    }

    public static boolean tryUpdateLatestEvent(String sceneId, int event, int eventStatus) {
        var scene = sceneEventControllers.get(sceneId);
        if (scene != null) {
            return scene.tryUpdateLatestEvent(event, eventStatus);
        }
        return false;
    }

    private static SceneEventControl buildSceneEventControl(String sceneId, int event, int eventStatus) {
        return new LocalSceneEventController(sceneId, event, eventStatus);
    }
}
