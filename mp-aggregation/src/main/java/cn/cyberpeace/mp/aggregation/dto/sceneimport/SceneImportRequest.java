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
public class SceneImportRequest {

    @ApiModelProperty("配置文件路径")
    private String path;

    @ApiModelProperty("镜像文件路径")
    private String imagePath;
}
