package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.enums.LogReportTypeEnum;

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
}
