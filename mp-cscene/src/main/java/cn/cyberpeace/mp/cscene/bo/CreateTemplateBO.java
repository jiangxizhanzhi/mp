package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CreateTemplateBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@ApiModel(description = "创建场景模板")
@Data
public class CreateTemplateBO {
    @ApiModelProperty(value = "源场景id")
    private String id;

    @ApiModelProperty(value = "场景名称")
    private String name;

    @ApiModelProperty(value = "logo")
    private String logo ;

    @ApiModelProperty(value = "场景描述")
    private String description;
}
