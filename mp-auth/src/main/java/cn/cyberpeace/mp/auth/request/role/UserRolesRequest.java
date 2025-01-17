 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserRolesRequest.java.
 // File size 525.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.request.role;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel(value = "绑定用户-角色关系请求（一个用户，多个角色）")
@Data
public class UserRolesRequest {

    @ApiModelProperty(value = "用户ID")
    @NotBlank
    String userId;

    @ApiModelProperty(value = "角色ID集合")
    List<String> roleIds;

}
