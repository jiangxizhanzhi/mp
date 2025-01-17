 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserRolePageRequest.java.
 // File size 391.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.request.role;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRolePageRequest extends PageRequest {

    @ApiModelProperty(value = "角色ID")
    private String roleId;

    @ApiModelProperty(value = "用户ID")
    private String userId;

}
