 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RoleUsersRequest.java.
 // File size 639.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.request.role;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName RoleUsersRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "绑定用户-角色关系请求(一个角色，多个用户)")
public class RoleUsersRequest {

    @NotBlank
    @ApiModelProperty(value = "角色ID")
    String roleId;

    @ApiModelProperty(value = "用户id列表")
    List<String> userIds;

}
