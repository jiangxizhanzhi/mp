package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@ApiModel
public class UserSimpleDto {

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
    List<OrgModel> orgs;

    @ApiModelProperty(value = "角色集合")
    List<RoleModel> roles;

}
