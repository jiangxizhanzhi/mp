package cn.cyberpeace.mp.scene.core.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ScenePluginDataBO {

    /**
     * key: pluginConfig
     */
    @ApiModelProperty(value = "本插件全局配置")
    private Map<String, Object> scene;

    /**
     * nodeId: pluginNodeData
     */
    @ApiModelProperty(value = "本插件网元配置")
    private Map<String, Map<String, Object>> elements;

    /**
     * portId: pluginPortData
     */
    @ApiModelProperty(value = "本插件端口配置")
    private Map<String, Map<String, Object>> elementPorts;

    /**
     * TODO: 这里插件的数据类型无法确定，暂未处理，目前插件实现的业务模型不绑定数据id
     */
    public ScenePluginDataBO export() {
        return this;
    }
}
