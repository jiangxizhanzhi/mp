package cn.cyberpeace.mp.scenepluginflow.service.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BusinessSceneCloudPlugin {
    @ApiModelProperty(value = "通用配置")
    private BusinessSceneCloudPluginScene scene;
    @ApiModelProperty(value = "节点配置(key为节点id，value 为节点配置内容)")
    private Map<String, BusinessSceneCloudPluginElement> elements;
    @ApiModelProperty(value = "节点配置(key为节点端口id，value 为节点配置内容)")
    private Map<String, BusinessSceneCloudPluginElementPort> elementPorts;
}
