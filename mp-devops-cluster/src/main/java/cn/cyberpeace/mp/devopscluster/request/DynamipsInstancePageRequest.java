 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DynamipsInstancePageRequest.java.
 // File size 675.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName DynamipsInstancePageRequest
 * @Description 实例分页请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see InstancePageRequest
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DynamipsInstancePageRequest extends InstancePageRequest {

    @ApiModelProperty(value = "排序字段")
    private String order;

    @ApiModelProperty(value = "升序:asc,倒序:desc")
    private String sort;
}
