package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserRequest extends UserModel {

    @ApiModelProperty(value = "组织集合")
    List<String> orgIds;

    @ApiModelProperty(value = "角色id列表")
    List<String> roleIds;

}
