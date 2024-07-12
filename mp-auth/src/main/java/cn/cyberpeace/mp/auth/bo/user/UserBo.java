 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserBo.java.
 // File size 1231.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.bo.user;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName UserBo
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see UserModel
 */
@Data
@ApiModel
public class UserBo extends UserModel {

    // 1：已过期  0：未过期，数据库中没有该字段，由expiredtime字段判断
    @ApiModelProperty(value = "1：已过期 0：未过期")
    private Integer isExpired;

    @ApiModelProperty(value = "是否是管理员 1：是 0：否")
    private Integer isAdmin;

    @ApiModelProperty(value = "是否是超级用户 1：是 0：否")
    private Integer isSuperUser;

    @ApiModelProperty(value = "组织路径")
    List<List<String>> orgpaths;

    @ApiModelProperty(value = "组织id集合")
    List<String> orgIds;

    @ApiModelProperty(value = "组织集合")
    List<OrgBO> orgs;

    @ApiModelProperty(value = "角色集合")
    List<RoleModel> roles;

}
