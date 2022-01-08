package cn.cyberpeace.mp.scene.core.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import java.util.Map;

@Data
public class BaseScenePluginDataBO<S, E, P> {

    /**
     * key: pluginConfig
     */
    @ApiModelProperty(value = "本插件全局配置")
    @Valid
    private S scene;

    /**
     * nodeId: pluginNodeData
     */
    @ApiModelProperty(value = "本插件网元配置")
    @Valid
    private Map<String, E> elements;

    /**
     * portId: pluginPortData
     */
    @ApiModelProperty(value = "本插件端口配置")
    @Valid
    @Length
    private Map<String, P> elementPorts;

    /**
     * TODO: 这里插件的数据类型无法确定，暂未处理，目前插件实现的业务模型不绑定数据id
     */
    public BaseScenePluginDataBO<S, E, P> export() {
        return this;
    }
}
