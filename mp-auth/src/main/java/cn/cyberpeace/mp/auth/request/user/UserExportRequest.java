 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserExportRequest.java.
 // File size 620.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UserExportRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Data
@ApiModel("导出用户请求")
public class UserExportRequest {

    @ApiModelProperty(value = "后台会匹配 username 或者 name")
    private String username;

    @ApiModelProperty(value = "组织id")
    private String org;

    @ApiModelProperty(value = "用户id列表")
    private String[] ids;

}
