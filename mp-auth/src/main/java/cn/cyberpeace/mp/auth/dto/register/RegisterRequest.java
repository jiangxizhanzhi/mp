package cn.cyberpeace.mp.auth.dto.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@ApiModel(value = "注册请求")
@Data
public class RegisterRequest {

    @ApiModelProperty(value = "用户名", example = "")
    @NotBlank(message = "username不能为空!")
    private String username;

    @ApiModelProperty(value = "密码 密码必须是DES加密后的密文", example = "")
    @NotBlank
    private String password;

    @ApiModelProperty(value = "姓名", example = "")
    private String name;

    @ApiModelProperty(value = "昵称", example = "")
    private String nickname;

    @ApiModelProperty(value = "头像地址 http或者https开头", example = "")
    private String avatar;

    @ApiModelProperty(value = "手机号", example = "")
    private String mobile;

    @ApiModelProperty(value = "注册短信码", example = "")
    private String smsCode;

    @ApiModelProperty(value = "邮箱", example = "")
    private String email;

}
