package cn.cyberpeace.mp.scene.core.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.Map;

@Data
public class BaseScenePluginDataBO<S, E, P> {

    
    @ApiModelProperty(value = "本插件全局配置")
    @Valid
    private S scene;

    
    @ApiModelProperty(value = "本插件网元配置")
    @Valid
    private Map<String, E> elements;

    
    @ApiModelProperty(value = "本插件端口配置")
    @Valid
    private Map<String, P> elementPorts;

    
    public BaseScenePluginDataBO<S, E, P> export() {
        return this;
    }
}
