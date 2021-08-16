package cn.cyberpeace.mp.auth.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActivationAuthItem {

    @ApiModelProperty(value = "产品/模块")
    private String model;

    @ApiModelProperty(value = "控制项名称")
    private String name;

    @ApiModelProperty(value = "数量")
    private Integer num;

}
