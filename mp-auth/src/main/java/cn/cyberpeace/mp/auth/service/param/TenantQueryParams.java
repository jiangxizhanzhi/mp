 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TenantQueryParams.java.
 // File size 472.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 租户查询参数定义
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantQueryParams extends QueryParams {

    private String name;

    private String identifier;

}
