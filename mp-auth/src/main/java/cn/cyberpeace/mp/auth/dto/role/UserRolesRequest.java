package cn.cyberpeace.mp.auth.dto.role;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel(value = "绑定用户-角色关系请求（一个用户，多个角色）")
@Data
public class UserRolesRequest {

    @ApiModelProperty(value = "用户ID")
    @NotBlank
    String userId;

    @ApiModelProperty(value = "角色ID集合")
    List<String> roleIds;

}
