package cn.cyberpeace.mp.scene.util;

import cn.cyberpeace.mp.scene.model.SceneModel;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

public class SceneEventUtils {
    public static final LoadingCache<String, Long> PAUSE_CACHE =
            CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(60, TimeUnit.SECONDS).build(new CacheLoader<String, Long>() {
        @Override
        public Long load(String key) throws Exception {
            return null;
        }
    });

    public static final LoadingCache<String, Long> UNPAUSE_CACHE =
            CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(60, TimeUnit.SECONDS).build(new CacheLoader<String, Long>() {
        @Override
        public Long load(String key) throws Exception {
            return null;
        }
    });

    public static void addProcessingScene(int event, String sceneId) {
        if (SceneModel.Event.PAUSE == event) {
            PAUSE_CACHE.put(sceneId, System.currentTimeMillis());
        } else if (SceneModel.Event.UNPAUSE == event) {
            UNPAUSE_CACHE.put(sceneId, System.currentTimeMillis());
        }
    }

    public static void removeProcessingScene(int event, String sceneId) {
        if (SceneModel.Event.PAUSE == event) {
            PAUSE_CACHE.invalidate(sceneId);
        } else if (SceneModel.Event.UNPAUSE == event) {
            UNPAUSE_CACHE.invalidate(sceneId);
        }
    }

    public static boolean hasProcessingScene(int event, String sceneId) {
        if (SceneModel.Event.PAUSE == event) {
            return PAUSE_CACHE.getIfPresent(sceneId) != null;
        } else if (SceneModel.Event.UNPAUSE == event) {
            return UNPAUSE_CACHE.getIfPresent(sceneId) != null;
        }
        return false;
    }
}
