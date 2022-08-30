package cn.cyberpeace.mp.auth.request.role;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel(value = "绑定用户-角色关系请求(一个角色，多个用户)")
public class RoleUsersRequest {

    @NotBlank
    @ApiModelProperty(value = "角色ID")
    String roleId;

    @ApiModelProperty(value = "用户id列表")
    List<String> userIds;

}
