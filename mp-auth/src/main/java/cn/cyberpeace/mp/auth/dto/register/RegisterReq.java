package cn.cyberpeace.mp.auth.dto.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "注册请求")
@Data
public class RegisterReq {

    @ApiModelProperty(value = "用户名", example = "")
    private String username;

    @ApiModelProperty(value = "密码", example = "")
    private String password;

    @ApiModelProperty(value = "姓名", example = "")
    private String name;

    @ApiModelProperty(value = "昵称", example = "")
    private String nickname;

    @ApiModelProperty(value = "手机号", example = "")
    private String mobile;

    @ApiModelProperty(value = "注册短信码", example = "")
    private String smsCode;

    @ApiModelProperty(value = "邮箱", example = "")
    private String email;

}
