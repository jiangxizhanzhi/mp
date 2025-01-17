 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BindUserTargetsRequest.java.
 // File size 583.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.request.target;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@ApiModel(value = "绑定用户-权限关系请求")
@Data
public class BindUserTargetsRequest {

    @NotBlank
    @ApiModelProperty(value = "用户ID")
    String userId;

    @ApiModelProperty(value = "权限ID列表")
    List<String> targetIds;

}
