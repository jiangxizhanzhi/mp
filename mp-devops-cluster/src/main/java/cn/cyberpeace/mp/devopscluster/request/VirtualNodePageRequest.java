 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualNodePageRequest.java.
 // File size 503.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName VirtualNodePageRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class VirtualNodePageRequest {
    private Integer page;
    private Integer perPage;
    private String name;
    private String nodeType;
    @ApiModelProperty(value = "是否查询全量数据")
    private Boolean isAll = true;
}
