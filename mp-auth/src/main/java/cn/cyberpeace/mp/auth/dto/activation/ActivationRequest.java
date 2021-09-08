package cn.cyberpeace.mp.auth.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
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

    @ApiModelProperty(value = "授权月份（多少个月）")
    @NotNull
    private Integer deadline;

    @ApiModelProperty(value = "授权功能项列表")
    private List<ActivationModuleItem> moduleItems;

    @ApiModelProperty(value = "授权资源项列表")
    private List<ActivationResourceItem> resourceItems;

    @ApiModelProperty(value = "校验码")
    @NotBlank
    private String verifyCode;



}
