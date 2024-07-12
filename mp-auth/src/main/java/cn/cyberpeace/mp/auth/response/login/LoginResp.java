 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LoginResp.java.
 // File size 659.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.response.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName LoginResp
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
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
