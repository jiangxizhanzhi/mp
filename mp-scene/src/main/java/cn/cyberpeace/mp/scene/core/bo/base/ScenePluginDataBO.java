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

    /**
     * TODO: 这里插件的数据类型无法确定，暂未处理，目前插件实现的业务模型不绑定数据id
     */
    public ScenePluginDataBO export() {
        return this;
    }
}
