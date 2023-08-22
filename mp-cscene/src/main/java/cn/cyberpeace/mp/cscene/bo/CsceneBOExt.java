package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "场景模板数据")
@Data
public class CsceneBOExt {
    @ApiModelProperty(value = "场景类型")
    String[] sceneBizType;
    @ApiModelProperty(value = "是否要转成模板")
    Boolean sceneConvertTemplate;
    @ApiModelProperty(value = "靶标类型")
    String[] imageCate;
}
