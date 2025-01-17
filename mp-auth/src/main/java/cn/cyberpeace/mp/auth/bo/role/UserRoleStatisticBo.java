 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserRoleStatisticBo.java.
 // File size 487.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "角色用户数量统计信息")
@Data
public class UserRoleStatisticBo {

    @ApiModelProperty(value = "角色ID")
    private String roleId;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "用户数量")
    private Integer userCnt;

}
