package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.log.service.LogFileService;
import cn.cyberpeace.mp.log.service.LogReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


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
    public List<LogReportDTO> list(String type) {
        return logReportService.list(type);
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
