package cn.cyberpeace.mp.auth.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 11:13
 */
@Data
@Builder
@Deprecated
public class VerifyInfoResponse {

    @ApiModelProperty(value = "机器码")
    private String machineCode;

    @ApiModelProperty(value = "授权月数")
    private Integer deadline;

    @ApiModelProperty(value = "激活版本 0:标准版 1:专业版")
    private Integer edition;

    @ApiModelProperty(value = "自定义授权信息json串")
    private String verifyInfo;

}
