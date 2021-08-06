package cn.cyberpeace.mp.scene.core.bo.base;

import lombok.Data;

import java.util.Map;

@Data
public abstract class BaseSceneDataBO<T> {
    private T topology;

    /**
     * pluginId: pluginData
     */
    private Map<String, ScenePluginDataBO> plugins;
}
