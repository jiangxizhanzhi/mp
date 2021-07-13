package cn.cyberpeace.mp.auth.dto.login;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
