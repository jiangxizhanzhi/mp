 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserSummarySceneResponse.java.
 // File size 814.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @ClassName UserSummarySceneResponse
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/9/13 11:09
 * @Version 1.0.0
 */
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
