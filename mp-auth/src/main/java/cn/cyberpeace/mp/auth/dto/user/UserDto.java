package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.beans.Transient;
import java.util.List;

@Data
@ApiModel
public class UserDto extends UserModel {


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
    List<OrgModel> orgs;

    @ApiModelProperty(value = "角色集合")
    List<RoleModel> roles;

}
