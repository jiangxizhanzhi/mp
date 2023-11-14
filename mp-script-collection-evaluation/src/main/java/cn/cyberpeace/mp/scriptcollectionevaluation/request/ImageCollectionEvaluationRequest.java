package cn.cyberpeace.mp.scriptcollectionevaluation.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImageCollectionEvaluationRequest {
    @ApiModelProperty(value = "镜像名称")
    private String name;
    @ApiModelProperty(value = "最大评估时间 单位:min")
    private Integer evaluationTime;
}
