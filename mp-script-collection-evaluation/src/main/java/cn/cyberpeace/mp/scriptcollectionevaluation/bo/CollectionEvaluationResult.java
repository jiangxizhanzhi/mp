package cn.cyberpeace.mp.scriptcollectionevaluation.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CollectionEvaluationResult {
    @ApiModelProperty("评估状态")
    private String status;
    @ApiModelProperty(value = "采集评估数据")
    private List<CollectionEvaluationBO> collectionEvaluations;
}
