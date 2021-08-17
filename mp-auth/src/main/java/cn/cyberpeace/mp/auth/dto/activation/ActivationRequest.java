package cn.cyberpeace.mp.auth.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ActivationRequest {

    @ApiModelProperty(value = "机器码")
    @NotBlank
    private String machinecode;

    @ApiModelProperty(value = "授权月份（多少个月）")
    @NotBlank
    private Integer deadline;

    @ApiModelProperty(value = "产品模块")
    @NotEmpty
    private List<String> products;

    @ApiModelProperty(value = "授权控制项列表")
    @NotEmpty
    private List<ActivationAuthItem> items;

    @ApiModelProperty(value = "校验码")
    @NotBlank
    private String verifyCode;

}
