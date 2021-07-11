package cn.cyberpeace.mp.scenepluginflow.util;

import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SceneFlowCacheUtil {
    private static LoadingCache<String, List<Flow>> cache = CacheBuilder.newBuilder()
            .maximumSize(300)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .build(new CacheLoader<String, List<Flow>>() {
                @Override
                public List<Flow> load(String key) {
                    String[] info = parseKey(key);
                    String sceneId = info[0];
                    String nodeId = info[1];
                    SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
                    if (sceneHandler != null) {
                        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
                        if (pluginHandler != null) {
                            var element = pluginHandler.getElement(nodeId);
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

    private static String getKey(String sceneId, String nodeId) {
        return sceneId + "_" + nodeId;
    }

    private static String[] parseKey(String key) {
        return StringUtils.split(key, "_");
    }
}
