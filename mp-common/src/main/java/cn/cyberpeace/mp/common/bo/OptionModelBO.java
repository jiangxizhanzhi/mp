package cn.cyberpeace.mp.common.bo;

import cn.cyberpeace.mp.common.model.OptionModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "选项")
public class OptionModelBO extends OptionModel {

    
    @ApiModelProperty(value = "名称")
    private String name;
}
