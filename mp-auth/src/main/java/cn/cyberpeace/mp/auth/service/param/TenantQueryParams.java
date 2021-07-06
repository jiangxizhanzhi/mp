package cn.cyberpeace.mp.auth.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 租户查询参数定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantQueryParams extends QueryParams {

    private String name;

    private String identifier;

}
