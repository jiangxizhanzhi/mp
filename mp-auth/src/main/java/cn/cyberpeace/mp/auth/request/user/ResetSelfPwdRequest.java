 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResetSelfPwdRequest.java.
 // File size 856.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.request.user;

import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.auth.request.validate.UpdateGroup;
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
