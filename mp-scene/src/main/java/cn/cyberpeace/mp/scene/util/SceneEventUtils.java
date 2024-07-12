 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneEventUtils.java.
 // File size 1244.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName SceneEventUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
