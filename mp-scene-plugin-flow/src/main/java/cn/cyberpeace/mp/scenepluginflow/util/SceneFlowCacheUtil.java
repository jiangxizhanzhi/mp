package cn.cyberpeace.mp.scenepluginflow.util;

import cn.cyberpeace.mp.base.util.mptool.ExceptionUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/1
 */
@Slf4j
public class SceneFlowCacheUtil {
    private static LoadingCache<String, SceneHandler> sceneCache = CacheBuilder.newBuilder()
            .maximumSize(300)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .build(new CacheLoader<String, SceneHandler>() {
                @Override
                public SceneHandler load(String sceneId) {
                    return new SceneHandler(sceneId, null, false, null);
                }
            });

    private static LoadingCache<String, List<Flow>> cache = CacheBuilder.newBuilder()
            .maximumSize(300)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .build(new CacheLoader<String, List<Flow>>() {
                @Override
                public List<Flow> load(String key) {
                    String[] info = parseKey(key);
                    String sceneId = info[0];
                    String nodeId = info[1];
                    SceneHandler sceneHandler = sceneCache.getUnchecked(sceneId);
                    if (sceneHandler != null) {
                        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
                        if (pluginHandler != null) {
                            FlowPluginSceneElementBO element = pluginHandler.getElement(nodeId);
                            if (element != null) {
                                return element.getFlows();
                            }
                        }
                    }
                    return new ArrayList<>();
                }
            });

    public static List<Flow> get(String sceneId, String nodeId) {
        if (StringUtils.isEmpty(sceneId) || StringUtils.isEmpty(nodeId)) {
            return new ArrayList<>();
        }
        return cache.getUnchecked(getKey(sceneId, nodeId));
    }

    public static void clearCache(String sceneId) {
        try {
            sceneCache.refresh(sceneId);
            var handler = sceneCache.get(sceneId);
            for (SceneElementBO e : handler.getElementMap().values()) {
                cache.refresh(getKey(sceneId, e.getId()));
            }
        } catch (Exception e) {
            log.error("failed to refresh cache , sceneId  is {} ,msg is {}", sceneId, ExceptionUtil.stacktraceToString(e));
        }
    }

    public static SceneHandler getSceneHandler(String sceneId) {
        return sceneCache.getUnchecked(sceneId);
    }
    
    private static String getKey(String sceneId, String nodeId) {
        return sceneId + "_" + nodeId;
    }

    private static String[] parseKey(String key) {
        return StringUtils.split(key, "_");
    }
}
