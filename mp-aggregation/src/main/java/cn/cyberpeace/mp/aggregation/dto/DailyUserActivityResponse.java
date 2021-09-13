package cn.cyberpeace.mp.aggregation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 11:03
 */
@Data
@Builder
@ApiModel("每日用户活动")
public class DailyUserActivityResponse {

    @ApiModelProperty(notes = "x轴")
    private String[] xAxis;

    @ApiModelProperty(notes = "用户数量")
    private Integer[] userCount;

    @ApiModelProperty(notes = "场景数量")
    private Integer[] sceneCount;

}
