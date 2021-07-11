package cn.cyberpeace.mp.auth.request.target;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


@ApiModel(value = "绑定用户-权限关系请求")
@Data
public class BindUserTargetsRequest {

    @NotBlank
    @ApiModelProperty(value = "用户ID")
    String userId;

    @ApiModelProperty(value = "权限ID列表")
    List<String> targetIds;

}
