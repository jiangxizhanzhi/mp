package cn.cyberpeace.mp.auth.request.user;

import cn.cyberpeace.mp.auth.model.UserModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("用户请求")
public class UserRequest extends UserModel {

    @ApiModelProperty(value = "组织id集合")
    List<String> orgIds;

    @ApiModelProperty(value = "角色id集合")
    List<String> roleIds;

}
