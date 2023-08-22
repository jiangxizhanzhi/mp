package cn.cyberpeace.mp.cscene.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "场景模板数据")
@Data
public class CsceneBOExt {

    @ApiModelProperty(value = "场景应用类型")
    @JsonProperty(value = "sceneAppType")
    String[] sceneAppType;
    @ApiModelProperty(value = "是否要转成模板")
    @JsonProperty(value = "sceneConvertTemplate")
    Boolean sceneConvertTemplate;
}
