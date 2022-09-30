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
