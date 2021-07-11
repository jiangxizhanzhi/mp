package cn.cyberpeace.mp.auth.request.user;

import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.auth.request.validate.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@ApiModel(value = "修改密码请求体")
@Data
public class ResetPwdRequest {

    @ApiModelProperty(value = "用户id，如果修改自己的密码，该参数会被忽略", example = "")
    @NotEmpty(groups = {AddGroup.class})
    private String userId;

    @ApiModelProperty(value = "新密码，必须是DES加密之后的密文", example = "")
    @NotEmpty(groups = {AddGroup.class, UpdateGroup.class})
    private String newPwd;

}
