 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserRolePageRequest.java.
 // File size 533.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.request.role;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UserRolePageRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see PageRequest
 */
@Data
public class UserRolePageRequest extends PageRequest {

    @ApiModelProperty(value = "角色ID")
    private String roleId;

    @ApiModelProperty(value = "用户ID")
    private String userId;

}
