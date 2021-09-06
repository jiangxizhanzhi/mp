package cn.cyberpeace.mp.auth.dto.org;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@ApiModel(value = "绑定组织-用户关系请求")
@Data
public class BindOrgUsersRequest {

    @NotBlank
    String orgId;

    List<String> userIds;

}
