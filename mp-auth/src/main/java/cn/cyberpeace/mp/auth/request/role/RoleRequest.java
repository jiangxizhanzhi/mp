 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RoleRequest.java.
 // File size 462.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.request.role;

import cn.cyberpeace.mp.auth.model.RoleModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName RoleRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see RoleModel
 */
@Data
public class RoleRequest extends RoleModel {

    @ApiModelProperty(value = "权限id集合")
    List<String> targetIds;

}
