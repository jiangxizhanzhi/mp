package cn.cyberpeace.mp.frontadmin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ActivationModuleItem {

    @ApiModelProperty(value = "应用模块ID")
    private String id;

    @ApiModelProperty(value = "唯一编号")
    private String identifier;

    @ApiModelProperty(value = "应用模块名称")
    private String name;

}
