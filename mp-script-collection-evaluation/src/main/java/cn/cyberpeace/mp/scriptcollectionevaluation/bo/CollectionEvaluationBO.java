package cn.cyberpeace.mp.scriptcollectionevaluation.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CollectionEvaluationBO {
    @ApiModelProperty(value = "采集项名称")
    private String name;
    @ApiModelProperty("是否支持")
    private Boolean support;
    @ApiModelProperty(value = "采集结果")
    private Object result;
}
