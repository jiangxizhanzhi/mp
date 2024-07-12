 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImageExportRequest.java.
 // File size 471.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.dto.sceneimport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName ImageExportRequest
 * @Description
 * @Author zhanglin
 * @Date 2022/09/19
 * @Version 1.0.0
 */
@Getter
@Setter
public class ImageExportRequest {
    @ApiModelProperty("镜像uuid")
    private String uuid;

    @ApiModelProperty("镜像文件导出路径")
    String imagePath;
}
