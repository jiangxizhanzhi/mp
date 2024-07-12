 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogReportRequest.java.
 // File size 659.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.request;

import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName LogReportRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class LogReportRequest {

    @ApiModelProperty(value = "日志上报类型")
    private LogReportTypeEnum logReportTypeEnum;

    @ApiModelProperty(value = "日志上报数据集合")
    private List<LogReportDTO> list;
}
