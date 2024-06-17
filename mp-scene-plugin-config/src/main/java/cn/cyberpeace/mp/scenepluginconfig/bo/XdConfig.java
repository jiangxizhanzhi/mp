package cn.cyberpeace.mp.scenepluginconfig.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class XdConfig {
    @ApiModelProperty(value = "配置项id")
    private String attributesId;
    @ApiModelProperty(value = "配置项code")
    private String attributeCode;
    @ApiModelProperty(value = "配置项名称")
    private String attributeName;
    @ApiModelProperty(value = "默认值")
    private String defaultValue;
    @ApiModelProperty(value = "父id")
    private String parentId;
    @ApiModelProperty(value = "模型id")
    private String modelId;
    @ApiModelProperty(value = "子配置项集合")
    private List<XdConfig> children;
}
