package cn.cyberpeace.mp.cscene.controller.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessCscene {
    @ApiModelProperty(value = "拓扑配置")
    private BusinssSceneConfig sceneConfig ;
}
