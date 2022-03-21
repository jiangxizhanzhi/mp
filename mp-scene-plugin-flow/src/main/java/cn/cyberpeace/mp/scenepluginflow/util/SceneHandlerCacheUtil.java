package cn.cyberpeace.mp.scenepluginflow.util;

import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/1
 */
public class SceneHandlerCacheUtil {
    private static LoadingCache<String, SceneHandler> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader<String, SceneHandler>() {
                @Override
                public SceneHandler load(String sceneId) {
                    return new SceneHandler(sceneId, null, false, null);
                }
            });

    public static SceneHandler get(String sceneId) {
        if (StringUtils.isEmpty(sceneId)) {
            return null;
        }
        return cache.getUnchecked(sceneId);
    }

    public static void clear(String sceneId) {
        if (StringUtils.isEmpty(sceneId)) {
            return;
        }
        cache.invalidate(sceneId);
    }
}
