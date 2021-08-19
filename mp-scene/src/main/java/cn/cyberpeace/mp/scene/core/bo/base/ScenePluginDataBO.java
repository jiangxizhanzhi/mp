package cn.cyberpeace.mp.scene.core.bo.base;

import lombok.Data;

import java.util.Map;

@Data
public class ScenePluginDataBO {

    /**
     * key: pluginConfig
     */
    private Map<String, Object> scene;

    /**
     * nodeId: pluginNodeData
     */
    private Map<String, Map<String, Object>> elements;

    /**
     * portId: pluginPortData
     */
    private Map<String, Map<String, Object>> elementPorts;

}
