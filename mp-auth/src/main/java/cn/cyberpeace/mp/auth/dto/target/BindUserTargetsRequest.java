package cn.cyberpeace.mp.auth.dto.target;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel(value = "绑定用户-权限关系请求")
@Data
public class BindUserTargetsRequest {

    @NotBlank
    String userId;

    List<String> targetIds;

}
