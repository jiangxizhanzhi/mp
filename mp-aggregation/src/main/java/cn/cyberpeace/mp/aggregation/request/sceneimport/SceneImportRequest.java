 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneImportRequest.java.
 // File size 687.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.request.sceneimport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName SceneImportRequest
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 * @Version 1.0.0
 */
@Getter
@Setter
public class SceneImportRequest {

    @ApiModelProperty("配置文件路径")
    private String path;

    @ApiModelProperty("镜像文件路径")
    private String imagePath;

    @ApiModelProperty("镜像文件路径列表")
    private List<String> imagePathList;

    @ApiModelProperty("场景文件路径")
    private String sceneFilePath;

    @ApiModelProperty("是否是场景模板")
    private Boolean template;
}
