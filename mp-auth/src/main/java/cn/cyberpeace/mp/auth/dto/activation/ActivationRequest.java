package cn.cyberpeace.mp.auth.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 激活请求体
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ActivationRequest {

    @ApiModelProperty(value = "系统序列号")
    @NotBlank
    private String machineCode;

    @ApiModelProperty(value = "授权月数")
    @NotNull
    private Integer deadline;

    @ApiModelProperty(value = "激活版本 0:标准版 1:专业版")
    @NotNull
    private Integer edition;

    @ApiModelProperty(value = "授权项列表")
    @NotEmpty
    private List<ActivationItem> items;

    @ApiModelProperty(value = "校验码")
    @NotBlank
    private String verifyCode;

}
