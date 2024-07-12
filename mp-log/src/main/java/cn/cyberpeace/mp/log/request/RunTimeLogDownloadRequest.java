 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RunTimeLogDownloadRequest.java.
 // File size 654.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RunTimeLogDownloadRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
