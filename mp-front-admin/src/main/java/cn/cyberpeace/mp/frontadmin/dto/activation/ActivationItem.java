package cn.cyberpeace.mp.frontadmin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ActivationItem {

    @ApiModelProperty(value = "授权项key")
    private String key;

    @ApiModelProperty(value = "授权项值")
    private String value;

    @ApiModelProperty(value = "1:授权功能  2:授权资源")
    private Integer type;

}
