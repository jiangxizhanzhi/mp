package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.devops.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
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
    List<LogReportDTO> list();

    /**
     * 下载
     * @param downloadDTO
     * @return
     */
    LogFileDownloadBO download(LogDownloadDTO downloadDTO) throws IOException;

}
