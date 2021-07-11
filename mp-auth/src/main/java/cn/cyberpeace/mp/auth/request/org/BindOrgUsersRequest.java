package cn.cyberpeace.mp.auth.request.org;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel(value = "绑定组织-用户关系请求")
@Data
public class BindOrgUsersRequest {

    @NotBlank
    @ApiModelProperty("组织ID")
    String orgId;

    @ApiModelProperty("用户ID列表")
    List<String> userIds;

}
