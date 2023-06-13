package cn.cyberpeace.mp.auth.response.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@ApiModel(value = "登录成功返回体")
@Data
public class LoginResp {

    @ApiModelProperty(value = "授权token", example = "")
    private String token;

    @ApiModelProperty(value = "用户信息", example = "")
    private Object user;

//    private Object roles;
    private Object targets;
//    private Object orgs;
    private Object menus;

    @ApiModelProperty(value = "是否需要同意隐私政策")
    private Boolean isNeedAgreePrivacyPolicy;

    @ApiModelProperty(value = "是否需要修改密码")
    private Boolean isNeedUpdatePassword;

}
