package cn.cyberpeace.mp.auth.dto.login;

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

}
