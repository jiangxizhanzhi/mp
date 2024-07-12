 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogFileDownloadUrlRequest.java.
 // File size 574.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.logfile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName LogFileDownloadUrlRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/5/7 10:04
 * @Version 1.0.0
 */
@Data
public class LogFileDownloadUrlRequest {

    @ApiModelProperty(value = "探针名称")
    private String agentName;

    @ApiModelProperty(value = "系统类型")
    private String osType;

    @ApiModelProperty(value = "路径")
    private List<String> paths;

}
