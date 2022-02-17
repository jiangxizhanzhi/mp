package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.core.CsceneInstanceHandler;
import cn.cyberpeace.mp.cscene.repository.CSceneElementCountMapper;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/16
 */
public final class NodeLimitUtils {
    private static Set<Integer> NODE_LIMIT_TYPES = new HashSet<>() {{
        this.add(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE);
        this.add(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER);
        this.add(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
    }};

    private static CSceneElementCountMapper countMapper;

    private static CsceneAppConfig csceneAppConfig;

    /**
     * 增加被限制的节点数量
     * @param instance
     * @return
     */
    public static boolean addLimitedNodeCount(CsceneInstanceHandler instance) {
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
    private static boolean addLimitedNodeCount(int count) {
        if (countMapper == null || csceneAppConfig == null) {
            countMapper = BeanCommon.getBean(CSceneElementCountMapper.class);
            csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        }
        int result = countMapper.updateElementCount(count, csceneAppConfig.getSceneUsingNodeLimit());
        return result > 0;
    }

    /**
     * 减小被限制的节点数量
     * @param count
     * @return
     */
    private static void substractLimitedNodeCount(int count) {
        if (countMapper == null || csceneAppConfig == null) {
            countMapper = BeanCommon.getBean(CSceneElementCountMapper.class);
            csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        }
        countMapper.updateElementCount(-1 * Math.abs(count), csceneAppConfig.getSceneUsingNodeLimit());
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
}
