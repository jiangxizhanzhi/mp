// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RegisterRequest.java.
 // File size 1138.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.request.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

 /**
  * 注册请求
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @ApiModel(value = "注册请求")
@Data
public class RegisterRequest {

    @ApiModelProperty(value = "用户名", example = "")
//    @NotBlank(message = "auth.validation.register.username")
    @NotBlank
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

    @ApiModelProperty(value = "邮箱", example = "")
    private String email;

    @ApiModelProperty(value = "验证码", example = "")
    private String verifyCode;

}
