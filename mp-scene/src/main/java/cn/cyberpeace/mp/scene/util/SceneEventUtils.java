package cn.cyberpeace.mp.scene.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SceneEventUtils {
    private static Map<String, SceneEventInfo> scene = new ConcurrentHashMap<>();

    /**
     * tryLockAndGetInfoIfLockFailed
     *
     * @param sceneId
     * @param eventInfo
     * @return
     */
    public static SceneEventInfo tryLockAndGetInfoIfLockFailed(String sceneId, SceneEventInfo eventInfo) {
        SceneEventInfo sceneEventInfo = scene.putIfAbsent(sceneId, eventInfo);
        if (sceneEventInfo != null) {
            return sceneEventInfo;
        } else {
            return null;
        }
    }

    /**
     * unLock
     *
     * @param sceneId
     */
    public static void unLock(String sceneId) {
        scene.remove(sceneId);
    }

    @Getter
    @Setter
    public static class SceneEventInfo {
        private String sceneId;
        private String nodeId;
        private String nodeName;
        private String eventName;
        private int event;
    }
}
