package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
public interface LogFileService extends Ordered {

    /**
     * 日志上报类型
     * @return
     */
    String logReportType();

    /**
     * 列表
     * @return
     */
    List<LogReportDTO> list(String type);

    /**
     * 下载
     * @param downloadDTO
     * @return
     */
    LogFileDownloadBO download(LogDownloadDTO downloadDTO) throws IOException;

}
