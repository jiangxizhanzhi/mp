 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AgentNameBasedRequest.java.
 // File size 929.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.agent;

import cn.cyberpeace.mp.cscene.request.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName AgentNameBasedRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/7/20 15:19
 * @Version 1.0.0
 * @see PageRequest
 */
@Data
public class AgentNameBasedRequest extends PageRequest {

    @ApiModelProperty("探针名称")
    private List<String> agentNames;

    @ApiModelProperty("开始时间, 默认当前时间往前推30分钟")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间，默认当前时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
