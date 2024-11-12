package cn.cyberpeace.mp.flow.service.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BusinessSceneTopology {
    @ApiModelProperty(value = "通用配置")
    private BusinessScene scene;
    @ApiModelProperty(value = "节点配置")
    private List<BusinessSceneElement> elements;
    @ApiModelProperty(value = "节点连线配置")
    private List<BusinessSceneCloudPluginElementPortConnect> connections;
}
