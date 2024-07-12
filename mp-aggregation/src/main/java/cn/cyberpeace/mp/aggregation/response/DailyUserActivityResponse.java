 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DailyUserActivityResponse.java.
 // File size 729.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName DailyUserActivityResponse
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/9/13 11:03
 * @Version 1.0.0
 */
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
