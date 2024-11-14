package cn.cyberpeace.mp.flow.integration.cloud.topocloud.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessCscene {
    @ApiModelProperty(value = "拓扑配置")
    private BusinssSceneConfig sceneConfig ;
}
