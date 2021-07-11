package cn.cyberpeace.mp.aggregation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("用户统计-场景")
public class UserSummarySceneResponse {

    @ApiModelProperty("场景数量")
    private Integer runningSceneCount;

    @ApiModelProperty("场景模板总数")
    private Integer sceneConfigCount;

    @ApiModelProperty("场景占比")
    private BigDecimal runningScenePercent;
}
