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
