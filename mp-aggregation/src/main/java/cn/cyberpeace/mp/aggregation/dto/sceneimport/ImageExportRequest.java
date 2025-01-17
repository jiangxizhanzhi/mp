package cn.cyberpeace.mp.aggregation.dto.sceneimport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 图像导出请求
 *
 * @author 二竹
 * @date 2025/01/17
 */
@Getter
@Setter
public class ImageExportRequest {
    @ApiModelProperty("镜像uuid")
    private String uuid;

    @ApiModelProperty("镜像文件导出路径")
    String imagePath;
}
