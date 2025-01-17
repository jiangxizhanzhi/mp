 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogReportService.java.
 // File size 590.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;

import java.util.List;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
public interface LogReportService {

    /**
     * 上报日志
     * @param logReportTypeEnum
     * @param list
     * @return
     */
    int report(LogReportTypeEnum logReportTypeEnum, List<LogReportDTO> list);

    /**
     * 查询全部日志上报数据
     * @return
     */
    List<LogReportDTO> list(String type);
}
