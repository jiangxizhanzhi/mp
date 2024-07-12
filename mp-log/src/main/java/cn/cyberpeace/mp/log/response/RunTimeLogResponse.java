 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RunTimeLogResponse.java.
 // File size 701.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.response;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.*;

/**
 * @ClassName RunTimeLogResponse
 * @Description
 * @Author zhangjin
 * @Date 9/13/21 9:30 上午
 * @Version 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunTimeLogResponse {
    @ApiModelProperty(value = "日志名称", required = true)
    private String logName;

    @ApiModelProperty(value = "日志文件大小", required = true)
    private Long size;

    @ApiModelProperty(value = "日志文件最新时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime lastModifyTime;
}
