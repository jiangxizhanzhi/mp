 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogReportRequest.java.
 // File size 614.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.request;

import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Data
@NoArgsConstructor
public class LogReportRequest {

    @ApiModelProperty(value = "日志上报类型")
    private LogReportTypeEnum logReportTypeEnum;

    @ApiModelProperty(value = "日志上报数据集合")
    private List<LogReportDTO> list;
}
