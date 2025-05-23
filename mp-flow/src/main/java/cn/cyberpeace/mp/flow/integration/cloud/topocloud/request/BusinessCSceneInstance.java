package cn.cyberpeace.mp.flow.integration.cloud.topocloud.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessCSceneInstance {
    @ApiModelProperty(value = "id")
    private String id;
    private String cscene;
    @ApiModelProperty(value = "拓扑配置")
    private BusinssSceneConfig scene;
}
