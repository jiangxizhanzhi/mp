 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BindRoleTargetsRequest.java.
 // File size 637.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.request.target;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName BindRoleTargetsRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@ApiModel(value = "绑定角色-权限关系请求")
@Data
public class BindRoleTargetsRequest {

    @NotBlank
    @ApiModelProperty(value = "角色ID")
    String roleId;

    @ApiModelProperty(value = "权限ID列表")
    List<String> targetIds;

}
