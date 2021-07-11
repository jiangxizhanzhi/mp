package cn.cyberpeace.mp.aggregation.sceneimport.parse.export;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ApiModel(value = "镜像导出的返回结果")
public class ImageExportResult {
    @ApiModelProperty(required = true, value = "是否成功")
    private boolean success = true;
}
