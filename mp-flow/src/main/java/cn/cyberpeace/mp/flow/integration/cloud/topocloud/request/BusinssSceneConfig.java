package cn.cyberpeace.mp.flow.integration.cloud.topocloud.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinssSceneConfig {
    @ApiModelProperty(value = "功能插件配置")
    private BusinessScenePlugin plugins;
    @ApiModelProperty(value = "拓扑结构配置")
    private BusinessSceneTopology topology;
}
