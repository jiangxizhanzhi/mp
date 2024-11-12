package cn.cyberpeace.mp.flow.service.business;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessSceneCloudPluginElementPortConnect {
    @ApiModelProperty(value = "连线id")
    private String id;
    @ApiModelProperty(value = "连线源节点id")
    private String source;
    @ApiModelProperty(value = "连线目标节点id")
    private String target;
    @ApiModelProperty(value = "连线名称")
    private String name;
}
