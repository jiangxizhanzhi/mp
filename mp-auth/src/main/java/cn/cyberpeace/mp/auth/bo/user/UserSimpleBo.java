 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserSimpleBo.java.
 // File size 1328.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.bo.user;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName UserSimpleBo
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Data
@ApiModel
public class UserSimpleBo {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "用户姓名")
    @DefaultStr
    private String name;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "用户名")
    private String username;

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
