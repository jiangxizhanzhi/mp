package cn.cyberpeace.mp.auth.dto.role;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel(value = "绑定用户-角色关系请求")
@Data
public class BindUserRolesRequest {

    @NotBlank
    String userId;

    List<String> roleIds;

}
