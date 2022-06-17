package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.devops.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.devops.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.devops.log.service.LogFileService;
import cn.cyberpeace.mp.devops.log.service.LogReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * MP中台日志模块
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportLogFileServiceImpl implements LogFileService {

    private final LogReportService logReportService;

    @Override
    public String logReportType() {
        return LogReportTypeEnum.REPORT.toValue();
    }

    @Override
    public List<LogReportDTO>  list() {
        return logReportService.list();
    }

    @Override
    public LogFileDownloadBO download(LogDownloadDTO downloadDTO) throws IOException{
        return null;
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
