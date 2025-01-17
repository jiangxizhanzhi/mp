 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RunTimeLogResponse.java.
 // File size 660.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.response;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.*;

/**
  *@Desciption 查询日志
  *@Author zhangjin
  *@Date 9/13/21 9:30 上午
**/
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
