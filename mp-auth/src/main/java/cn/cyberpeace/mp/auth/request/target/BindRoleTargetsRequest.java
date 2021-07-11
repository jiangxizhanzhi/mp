package cn.cyberpeace.mp.auth.request.target;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


@ApiModel(value = "绑定角色-权限关系请求")
@Data
public class BindRoleTargetsRequest {

    @NotBlank
    @ApiModelProperty(value = "角色ID")
    String roleId;

    @ApiModelProperty(value = "权限ID列表")
    List<String> targetIds;

}
