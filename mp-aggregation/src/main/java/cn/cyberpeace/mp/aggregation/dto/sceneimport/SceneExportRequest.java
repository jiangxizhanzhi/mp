package cn.cyberpeace.mp.aggregation.dto.sceneimport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 */
@Getter
@Setter
public class SceneExportRequest {

    @ApiModelProperty("场景id")
    private String id;

    @ApiModelProperty("场景文件导出路径")
    String envPath;

    @ApiModelProperty("镜像文件导出路径")
    String imagePath;

    @ApiModelProperty("单个镜像文件导出路径")
    String singleImagePath;
}
