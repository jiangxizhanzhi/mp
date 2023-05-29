package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.core.CsceneInstanceHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/16
 */
@Slf4j
public final class SceneLimitUtils {
    private static int SCENE_COUNT = 0;

    private static int SCENE_USING_NODE_LIMIT = -1;

    private static CsceneAppConfig csceneAppConfig;

    /**
     * 增加被限制的节点数量
     *
     * @param instance
     * @return
     */
    public static UpdateResult addLimitedSceneCount(CsceneInstanceHandler instance) {
        return addLimitedSceneCount(1);
    }

    /**
     * 减小被限制的节点数量
     *
     * @param instance
     * @return
     */
    public static void substractLimitedSceneCount(CsceneInstanceHandler instance) {
        substractLimitedSceneCount(1);
    }

    /**
     * 增加被限制的节点数量
     *
     * @param count
     * @return
     */
    public static UpdateResult addLimitedSceneCount(int count) {
        if (csceneAppConfig == null) {
            csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        }
        return updateSceneCount(count);
    }

    public synchronized static void refreshCount(int count) {
        if (csceneAppConfig == null) {
            csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        }
        log.info("refresh node limit count , origin count: {} , {}", SCENE_USING_NODE_LIMIT, SCENE_COUNT);
        SCENE_USING_NODE_LIMIT = csceneAppConfig.getSceneUsingNodeLimit();
        SCENE_COUNT = count;
        log.info("refresh node limit count , new count: {} , {}", SCENE_USING_NODE_LIMIT, count);
    }

    private synchronized static UpdateResult updateSceneCount(int count) {
        if (SCENE_USING_NODE_LIMIT < 0) {
            SCENE_USING_NODE_LIMIT = csceneAppConfig.getSceneUsingNodeLimit();
        }
        log.info("current scene num {},update scene {},limit num {}", SCENE_COUNT, count, SCENE_USING_NODE_LIMIT);
        if (SCENE_COUNT + count > SCENE_USING_NODE_LIMIT) {
            UpdateResult updateResult = new UpdateResult();
            updateResult.setSuccess(false);
            updateResult.setLimitNode(csceneAppConfig.getSceneUsingNodeLimit());
            updateResult.setCurrentNode(count);
            updateResult.setExistNode(SCENE_COUNT);
            return updateResult;
        }
        SCENE_COUNT = SCENE_COUNT + count;
        if (SCENE_COUNT < 0) {
            SCENE_COUNT = 0;
        }
        UpdateResult updateResult = new UpdateResult();
        updateResult.setSuccess(true);
        return updateResult;
    }

    /**
     * 减小被限制的节点数量
     *
     * @param count
     * @return
     */
    private static void substractLimitedSceneCount(int count) {
        if (csceneAppConfig == null) {
            csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        }
        updateSceneCount(-1 * Math.abs(count));
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
