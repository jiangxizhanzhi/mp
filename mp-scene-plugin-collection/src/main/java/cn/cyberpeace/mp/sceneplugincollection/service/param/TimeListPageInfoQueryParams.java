 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TimeListPageInfoQueryParams.java.
 // File size 541.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.service.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName TimeListPageInfoQueryParams
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ListPageInfoQueryParams
 */
@Data
public class TimeListPageInfoQueryParams extends ListPageInfoQueryParams {
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
