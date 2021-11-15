package cn.cyberpeace.mp.front.admin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 11:13
 */
@Data
@Deprecated
public class VerifyInfoRequest {

    @ApiModelProperty(value = "授权月数")
    @NotNull
    private Integer deadline;

    @ApiModelProperty(value = "激活版本 0:标准版 1:专业版")
    @NotNull
    private Integer edition;

    @ApiModelProperty(value = "授权功能项列表")
    @NotEmpty
    private List<ActivationModuleItem> moduleItems;

    @ApiModelProperty(value = "授权资源项列表")
    @NotEmpty
    private List<ActivationResourceItem> resourceItems;

}
