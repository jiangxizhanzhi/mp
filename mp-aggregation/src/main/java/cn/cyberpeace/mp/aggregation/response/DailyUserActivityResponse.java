package cn.cyberpeace.mp.aggregation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@ApiModel("每日用户活动响应")
public class DailyUserActivityResponse {

    @ApiModelProperty(value = "x轴", notes = "x轴")
    private String[] xAxis;

    @ApiModelProperty(value = "用户数量", notes = "用户数量")
    private Integer[] userCount;

    @ApiModelProperty(value = "场景数量", notes = "场景数量")
    private Integer[] sceneCount;

}
