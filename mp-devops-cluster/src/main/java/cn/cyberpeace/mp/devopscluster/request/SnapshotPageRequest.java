 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnapshotPageRequest.java.
 // File size 525.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SnapshotPageRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SnapshotPageRequest {
    @ApiModelProperty(value = "分页页码")
    private Integer page = 1;
    @ApiModelProperty(value = "分页大小")
    private Integer perPage = 10;
    @ApiModelProperty(value = "实例id")
    private String instanceId;
}
