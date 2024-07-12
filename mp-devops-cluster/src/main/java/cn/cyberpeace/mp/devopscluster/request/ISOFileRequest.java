 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ISOFileRequest.java.
 // File size 424.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ISOFileRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class ISOFileRequest {
    @ApiModelProperty("临时文件uuid")
    private String tmpdirUuid;
    @ApiModelProperty("文件名称")
    private String fileName;
}
