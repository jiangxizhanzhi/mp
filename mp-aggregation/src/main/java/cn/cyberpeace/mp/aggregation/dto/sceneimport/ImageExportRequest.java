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
