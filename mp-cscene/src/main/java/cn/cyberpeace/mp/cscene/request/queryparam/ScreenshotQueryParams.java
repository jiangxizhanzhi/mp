 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScreenshotQueryParams.java.
 // File size 754.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ScreenshotQueryParams
 * @Description 截屏通用查询条件
 * @Author qiaokang
 * @Date 2021/8/31 17:17
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@ApiModel(value = "截屏通用查询条件")
public class ScreenshotQueryParams extends QueryParams {

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

}
