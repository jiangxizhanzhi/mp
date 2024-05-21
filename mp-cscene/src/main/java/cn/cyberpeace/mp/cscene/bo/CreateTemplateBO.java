package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "创建场景模板")
@Data
public class CreateTemplateBO {
    @ApiModelProperty(value = "源场景id")
    private String id;

    @ApiModelProperty(value = "场景名称")
    private String name;

    @ApiModelProperty(value = "场景描述")
    private String description;
}
