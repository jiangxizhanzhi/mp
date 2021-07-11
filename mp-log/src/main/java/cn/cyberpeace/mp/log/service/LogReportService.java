package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;

import java.util.List;


public interface LogReportService {

    
    int report(LogReportTypeEnum logReportTypeEnum, List<LogReportDTO> list);

    
    List<LogReportDTO> list(String type);
}
