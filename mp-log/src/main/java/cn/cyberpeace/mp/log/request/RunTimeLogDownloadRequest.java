 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RunTimeLogDownloadRequest.java.
 // File size 600.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Data
@NoArgsConstructor
public class RunTimeLogDownloadRequest {

    @ApiModelProperty(value = "日志上报类型")
    private String type;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "节点名称")
    private String hostname;

    @ApiModelProperty(value = "路径")
    private String path;
}
