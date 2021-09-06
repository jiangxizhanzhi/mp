package cn.cyberpeace.mp.auth.dto;

import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.dto.validate.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
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
