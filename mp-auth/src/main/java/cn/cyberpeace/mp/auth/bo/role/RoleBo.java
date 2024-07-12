 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RoleBo.java.
 // File size 614.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.bo.role;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName RoleBo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see RoleModel
 */
@Data
public class RoleBo extends RoleModel {

    @ApiModelProperty(value = "角色绑定的用户数量")
    private Integer userCnt;

    @ApiModelProperty(value = "角色绑定的权限列表")
    private List<TargetModel> targets;

}
