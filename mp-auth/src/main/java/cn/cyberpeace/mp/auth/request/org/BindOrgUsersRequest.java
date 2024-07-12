 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BindOrgUsersRequest.java.
 // File size 609.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.request.org;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName BindOrgUsersRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@ApiModel(value = "绑定组织-用户关系请求")
@Data
public class BindOrgUsersRequest {

    @NotBlank
    @ApiModelProperty("组织ID")
    String orgId;

    @ApiModelProperty("用户ID列表")
    List<String> userIds;

}
