 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneExportRequest.java.
 // File size 791.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.dto.sceneimport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName SceneExportRequest
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 * @Version 1.0.0
 */
@Getter
@Setter
public class SceneExportRequest {

    @ApiModelProperty("场景id")
    private String id;

    @ApiModelProperty("场景编号")
    private String sceneNumber;

    @ApiModelProperty("场景文件导出路径")
    String envPath;

    @ApiModelProperty("镜像文件导出路径")
    String imagePath;

    @ApiModelProperty("单个镜像文件导出路径")
    String singleImagePath;

    @ApiModelProperty("是否禁止导出镜像")
    Boolean disableImageExport;

}
