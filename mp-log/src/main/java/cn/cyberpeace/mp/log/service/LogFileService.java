package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.List;


public interface LogFileService extends Ordered {

    
    String logReportType();

    
    List<LogReportDTO> list(String type);

    
    LogFileDownloadBO download(LogDownloadDTO downloadDTO) throws IOException;

}
