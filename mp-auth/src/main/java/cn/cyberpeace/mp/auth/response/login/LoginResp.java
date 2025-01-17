 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LoginResp.java.
 // File size 618.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
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

}
