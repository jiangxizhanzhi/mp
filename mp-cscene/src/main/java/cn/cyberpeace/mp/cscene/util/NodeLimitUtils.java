package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.core.CsceneInstanceHandler;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Slf4j
public final class NodeLimitUtils {
    private static int NODE_COUNT = 0;

    private static Set<Integer> NODE_LIMIT_TYPES = new HashSet<>() {{
        this.add(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE);
        this.add(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER);
        this.add(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
        this.add(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS);
    }};

    private static CsceneAppConfig csceneAppConfig;

    
    public static UpdateResult addLimitedNodeCount(CsceneInstanceHandler instance) {
        int limitedNodeCount = countVirtualElement(instance);
        return addLimitedNodeCount(limitedNodeCount);
    }

    
    public static void substractLimitedNodeCount(CsceneInstanceHandler instance) {
        int limitedNodeCount = countVirtualElement(instance);
        substractLimitedNodeCount(limitedNodeCount);
    }

    
    public static UpdateResult addLimitedNodeCount(int count) {
        if (csceneAppConfig == null) {
            csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        }
        return updateElementCount(count);
    }

    public synchronized static void refreshCount(int count) {
        if (csceneAppConfig == null) {
            csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        }
        int SCENE_USING_NODE_LIMIT = csceneAppConfig.getSceneUsingNodeLimit();
        log.info("refresh node limit count , origin count: {} , {}", SCENE_USING_NODE_LIMIT, NODE_COUNT);
        SCENE_USING_NODE_LIMIT = csceneAppConfig.getSceneUsingNodeLimit();
        NODE_COUNT = count;
        log.info("refresh node limit count , new count: {} , {}", SCENE_USING_NODE_LIMIT, count);
    }

    private synchronized static UpdateResult updateElementCount(int count) {
        log.info("current node num {},update num {},limit num {}", NODE_COUNT, count, csceneAppConfig.getSceneUsingNodeLimit());
        if (count > 0) {
            if (NODE_COUNT + count > csceneAppConfig.getSceneUsingNodeLimit()) {
                UpdateResult updateResult = new UpdateResult();
                updateResult.setSuccess(false);
                updateResult.setLimitNode(csceneAppConfig.getSceneUsingNodeLimit());
                updateResult.setCurrentNode(count);
                updateResult.setExistNode(NODE_COUNT);
                return updateResult;
            }
        }
        NODE_COUNT = NODE_COUNT + count;
        if (NODE_COUNT < 0) {
            NODE_COUNT = 0;
        }
        UpdateResult updateResult = new UpdateResult();
        updateResult.setSuccess(true);
        return updateResult;
    }

    
    private static void substractLimitedNodeCount(int count) {
        if (csceneAppConfig == null) {
            csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        }
        updateElementCount(-1 * Math.abs(count));
    }

    
    private static int countVirtualElement(CsceneInstanceHandler handler) {
        if (handler == null || handler.getCscene() == null || handler.getCscene().getScene() == null) {
            return 0;
        }
        Map<String, ScenePluginDataBO> plugins = handler.getCscene().getScene().getPlugins();
        if (plugins == null || plugins.get("cpcloud") == null || plugins.get("cpcloud").getElements() == null) {
            return 0;
        }
        int result = 0;
        for (Map<String, Object> map : plugins.get("cpcloud").getElements().values()) {
            Integer virtualType = (Integer) map.get("virtual_type");
            if (virtualType != null && NODE_LIMIT_TYPES.contains(virtualType)) {
                result++;
            }
        }
        return result;
    }

    @Getter
    @Setter
    public static class UpdateResult {
        private boolean success;
        private int limitNode;
        private int currentNode;
        private int existNode;
    }

    public static int calLimitedNodesNum(CsceneBO cscene) {
        int elementCount = 0;
        if (cscene != null && cscene.getSceneConfig() != null && cscene.getSceneConfig().getPlugins() != null
                && cscene.getSceneConfig().getPlugins().containsKey(PluginConstant.PLUGIN_KEY)) {
            var pluginElements = cscene.getSceneConfig().getPlugins().get(PluginConstant.PLUGIN_KEY).getElements();
            if (pluginElements != null) {
                for (var e : pluginElements.values()) {
                    var virtualType = e.get("virtual_type");
                    if (virtualType != null) {
                        if (isLimitedNode(Integer.parseInt(virtualType.toString()))) {
                            elementCount++;
                        }
                    }
                }
            }
        }
        return elementCount;
    }

    public static boolean isLimitedNode(Integer virtualType) {
        if (virtualType == null) {
            return false;
        }
        if (virtualType == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER ||
                virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE ||
                virtualType == BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE ||
                virtualType == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER ||
                virtualType == BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS) {
            return true;
        }
        return false;
    }
}
