package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserDto extends UserModel {

    @ApiModelProperty(value = "组织路径")
    String orgpath;

    @ApiModelProperty(value = "角色集合")
    List<RoleModel> roles;

    @ApiModelProperty(value = "组织id集合")
    List<String> orgIds;

    @ApiModelProperty(value = "角色id集合")
    List<String> roleIds;

}
