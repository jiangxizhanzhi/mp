 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogReportService.java.
 // File size 671.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;

import java.util.List;

/**
 * @ClassName LogReportService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface LogReportService {

    /**
     * 上报日志
     *
     * @param logReportTypeEnum
     * @param list
     * @return
     */
    int report(LogReportTypeEnum logReportTypeEnum, List<LogReportDTO> list);

    /**
     * 查询全部日志上报数据
     *
     * @param type
     * @return
     */
    List<LogReportDTO> list(String type);
}
