 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneFlowCacheUtil.java.
 // File size 3327.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.util;

import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
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

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/1
 */
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

    /**
     * get
     *
     * @param sceneId
     * @param nodeId
     * @return
     */
    public static List<Flow> get(String sceneId, String nodeId) {
        if (StringUtils.isEmpty(sceneId) || StringUtils.isEmpty(nodeId)) {
            return new ArrayList<>();
        }
        return cache.getUnchecked(getKey(sceneId, nodeId));
    }

    /**
     * getSceneHandler
     *
     * @param sceneId
     * @return 场景实例的处理
     */
    public static SceneHandler getSceneHandler(String sceneId) {
        return sceneCache.getUnchecked(sceneId);
    }

    /**
     * getKey
     *
     * @param sceneId
     * @param nodeId
     * @return
     */
    private static String getKey(String sceneId, String nodeId) {
        return sceneId + "_" + nodeId;
    }

    /**
     * parseKey
     *
     * @param key
     * @return
     */
    private static String[] parseKey(String key) {
        return StringUtils.split(key, "_");
    }
}
