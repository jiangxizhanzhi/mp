 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HostStateTrendRequest.java.
 // File size 976.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.hoststate;

import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @ClassName HostStateTrendRequest
 * @Description 资源使用请求体
 * @Author zhouxinghuai
 * @Date 2021/11/24 14:22
 * @Version 1.0.0
 * @see BaseSceneElementRequest
 */
@Data
public class HostStateTrendRequest extends BaseSceneElementRequest {

    @ApiModelProperty("开始时间, 默认当前时间往前推30分钟")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间，默认当前时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty("分段数，默认10段")
    private Integer segmentCount;

}
