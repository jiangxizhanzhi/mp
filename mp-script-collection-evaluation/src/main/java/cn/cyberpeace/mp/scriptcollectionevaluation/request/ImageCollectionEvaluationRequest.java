package cn.cyberpeace.mp.scriptcollectionevaluation.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImageCollectionEvaluationRequest {
    @ApiModelProperty(value = "镜像名称")
    private String name;
    @ApiModelProperty(value = "检测间隔 单位:min")
    private Integer interval = 6;
    @ApiModelProperty(value = "检测次数")
    private Integer frequency = 1;
}
