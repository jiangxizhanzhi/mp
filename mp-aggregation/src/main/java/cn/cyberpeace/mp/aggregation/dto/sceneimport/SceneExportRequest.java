package cn.cyberpeace.mp.aggregation.dto.sceneimport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 场景导出请求
 *
 * @author 二竹
 * @date 2025/01/17
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
