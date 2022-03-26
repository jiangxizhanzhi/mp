package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.core.CsceneInstanceHandler;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/16
 */
@Slf4j
public final class NodeLimitUtils {
    private static int NODE_COUNT = 0;

    private static int SCENE_USING_NODE_LIMIT = -1;

    private static Set<Integer> NODE_LIMIT_TYPES = new HashSet<>() {{
        this.add(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE);
        this.add(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER);
        this.add(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
    }};

    private static CsceneAppConfig csceneAppConfig;

    /**
     * 增加被限制的节点数量
     * @param instance
     * @return
     */
    public static UpdateResult addLimitedNodeCount(CsceneInstanceHandler instance) {
        int limitedNodeCount = countVirtualElement(instance);
        return addLimitedNodeCount(limitedNodeCount);
    }

    /**
     * 减小被限制的节点数量
     * @param instance
     * @return
     */
    public static void substractLimitedNodeCount(CsceneInstanceHandler instance) {
        int limitedNodeCount = countVirtualElement(instance);
        substractLimitedNodeCount(limitedNodeCount);
    }

    /**
     * 增加被限制的节点数量
     * @param count
     * @return
     */
    public static UpdateResult addLimitedNodeCount(int count) {
        if (csceneAppConfig == null) {
            csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        }
        return updateElementCount(count);
    }

    public synchronized static void refreshCount(int count) {
        log.info("refresh node limit count , origin count: {} , {}", SCENE_USING_NODE_LIMIT, NODE_COUNT);
        SCENE_USING_NODE_LIMIT = csceneAppConfig.getSceneUsingNodeLimit();
        NODE_COUNT = count;
        log.info("refresh node limit count , new count: {} , {}", SCENE_USING_NODE_LIMIT, count);
    }

    private synchronized static UpdateResult updateElementCount(int count) {
        if (SCENE_USING_NODE_LIMIT < 0) {
            SCENE_USING_NODE_LIMIT = csceneAppConfig.getSceneUsingNodeLimit();
        }
        log.info("current node num {},update num {},limit num {}", NODE_COUNT, count, SCENE_USING_NODE_LIMIT);
        if (NODE_COUNT + count > SCENE_USING_NODE_LIMIT) {
            UpdateResult updateResult = new UpdateResult();
            updateResult.setSuccess(false);
            updateResult.setLimitNode(csceneAppConfig.getSceneUsingNodeLimit());
            updateResult.setCurrentNode(count);
            updateResult.setExistNode(NODE_COUNT);
            return updateResult;
        }
        NODE_COUNT = NODE_COUNT + count;
        if (NODE_COUNT < 0) {
            NODE_COUNT = 0;
        }
        UpdateResult updateResult = new UpdateResult();
        updateResult.setSuccess(true);
        return updateResult;
    }

    /**
     * 减小被限制的节点数量
     * @param count
     * @return
     */
    private static void substractLimitedNodeCount(int count) {
        if (csceneAppConfig == null) {
            csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        }
        updateElementCount(-1 * Math.abs(count));
    }

    /**
     * 计算限制的数量
     * @param handler
     * @return
     */
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
}
