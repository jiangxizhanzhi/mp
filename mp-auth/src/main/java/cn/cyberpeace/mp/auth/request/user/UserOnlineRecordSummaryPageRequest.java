 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOnlineRecordSummaryPageRequest.java.
 // File size 658.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.request.user;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class UserOnlineRecordSummaryPageRequest extends PageRequest {

    @ApiModelProperty(value = "用户ID列表")
    private List<String> userIds;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

}
