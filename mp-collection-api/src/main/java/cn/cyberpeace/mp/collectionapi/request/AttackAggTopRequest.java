package cn.cyberpeace.mp.collectionapi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AttackAggTopRequest extends AttackFlowBaseRequest {
    @ApiModelProperty(value = "topSize")
    private Integer topSize;
}
