package cn.cyberpeace.mp.auth.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户查询参数定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQueryParams extends QueryParams {

    private String tenant;

    private String username;

    private String name;

    private Integer status;

}
