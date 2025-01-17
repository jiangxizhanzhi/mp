 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlAccessCtlRequest.java.
 // File size 1108.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.request.urlac;

import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.auth.request.validate.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlAccessCtlRequest {

    @NotBlank(groups = {UpdateGroup.class})
    @ApiModelProperty(value = "ID")
    private String id;

    @NotBlank(groups = AddGroup.class)
    @ApiModelProperty(value = "请求url")
    private String url;

    @NotEmpty(groups = AddGroup.class)
    @ApiModelProperty(value = "请求方法")
    private List<String> methods;

    @NotEmpty(groups = AddGroup.class)
    @ApiModelProperty(value = "权限列表")
    private List<String> targetIds;

}
