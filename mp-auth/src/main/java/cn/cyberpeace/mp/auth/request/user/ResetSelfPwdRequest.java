 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResetSelfPwdRequest.java.
 // File size 907.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.request.user;

import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.auth.request.validate.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @ClassName ResetSelfPwdRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
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
