 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseTimeRangeRequest.java.
 // File size 790.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @ClassName BaseTimeRangeRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/03/14 14:22
 * @Version 1.0.0
 * @see BaseSceneElementRequest
 */
@Data
public class BaseTimeRangeRequest extends BaseSceneElementRequest {

    @ApiModelProperty("开始时间, 默认当前时间往前推30分钟")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间，默认当前时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
