 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RoleRequest.java.
 // File size 330.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.request.role;

import cn.cyberpeace.mp.auth.model.RoleModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoleRequest extends RoleModel {

    @ApiModelProperty(value = "权限id集合")
    List<String> targetIds;

}
