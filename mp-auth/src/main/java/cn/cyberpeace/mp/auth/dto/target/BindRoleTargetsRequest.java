package cn.cyberpeace.mp.auth.dto.target;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@ApiModel(value = "绑定角色-权限关系请求")
@Data
public class BindRoleTargetsRequest {

    @NotBlank
    String roleId;

    List<String> targetIds;

}
