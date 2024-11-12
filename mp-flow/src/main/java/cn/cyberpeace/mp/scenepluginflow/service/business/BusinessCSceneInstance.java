package cn.cyberpeace.mp.scenepluginflow.service.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessCSceneInstance {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "拓扑配置")
    private BusinssSceneConfig scene;
}
