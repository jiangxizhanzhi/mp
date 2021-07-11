package cn.cyberpeace.mp.auth.bo.user;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


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
