package cn.cyberpeace.mp.aggregation.dto.sceneimport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ImageExportRequest {
    @ApiModelProperty("镜像uuid")
    private String uuid;

    @ApiModelProperty("镜像文件导出路径")
    String imagePath;
}
