package cn.cyberpeace.mp.aggregation.request.sceneimport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


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
}
