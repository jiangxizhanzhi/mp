 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RoleBo.java.
 // File size 487.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.role;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoleBo extends RoleModel {

    @ApiModelProperty(value = "角色绑定的用户数量")
    private Integer userCnt;

    @ApiModelProperty(value = "角色绑定的权限列表")
    private List<TargetModel> targets;

}
