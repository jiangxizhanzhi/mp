 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserQueryParams.java.
 // File size 597.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName UserQueryParams
 * @Description 用户查询参数定义
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see QueryParams
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
