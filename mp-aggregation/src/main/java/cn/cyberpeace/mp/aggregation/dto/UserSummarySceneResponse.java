package cn.cyberpeace.mp.aggregation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 11:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("用户统计-场景")
public class UserSummarySceneResponse {

    @ApiModelProperty("场景数量")
    private Integer runningSceneCount;

    @ApiModelProperty("场景占比")
    private BigDecimal runningScenePercent;
}
