 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RunTimeLogDTO.java.
 // File size 1265.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.dto;

import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
  *@Desciption 查询日志
  *@Author zhangjin
  *@Date 9/13/21 9:30 上午
**/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunTimeLogDTO {

    @ApiModelProperty(value = "日志名称", required = true)
    private String logName;

    @ApiModelProperty(value = "日志文件大小", required = true)
    private Long size;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private String id;

    /**
     * 下载地址
     */
    @ApiModelProperty(value = "下载地址")
    private String downloadUrl;

    /**
     * 节点ip
     */
    @ApiModelProperty(value = "节点ip")
    private String hostIp;

    /**
     * 日志类型
     */
    @ApiModelProperty(value = "日志类型")
    private LogReportTypeEnum logReportTypeEnum;

    @ApiModelProperty(value = "日志文件最新时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime lastModifyTime;

}
