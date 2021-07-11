package cn.cyberpeace.mp.auth.request.user;

import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.auth.request.validate.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@ApiModel(value = "修改本人密码请求体")
@Data
public class ResetSelfPwdRequest {

    @ApiModelProperty(value = "旧密码，必须是DES加密之后的密文", example = "")
    @NotEmpty(groups = {AddGroup.class})
    private String oldPwd;

    @ApiModelProperty(value = "新密码，必须是DES加密之后的密文", example = "")
    @NotEmpty(groups = {AddGroup.class, UpdateGroup.class})
    private String newPwd;

}
