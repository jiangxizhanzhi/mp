package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;

import java.util.List;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
public interface LogFileService {

    /**
     * 日志上报类型
     * @return
     */
    String logReportType();

    /**
     * 列表
     * @return
     */
    List<LogReportDTO>  list();

    /**
     * 下载
     * @param fileName
     * @return
     */
    LogFileDownloadBO download(String fileName);

}
