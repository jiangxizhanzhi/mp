package cn.cyberpeace.mp.auth.dto.role;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel(value = "绑定用户-角色关系请求(一个角色，多个用户)")
@Data
public class BindRoleUsersRequest {

    @NotBlank
    String roleId;

    List<String> userIds;

}
