 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogFileService.java.
 // File size 844.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName LogFileService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Ordered
 */
public interface LogFileService extends Ordered {

    /**
     * 日志上报类型
     *
     * @return
     */
    String logReportType();

    /**
     * 列表
     *
     * @return
     */
    List<LogReportDTO> list(String type);

    /**
     * 下载
     *
     * @param downloadDTO
     * @return
     */
    LogFileDownloadBO download(LogDownloadDTO downloadDTO) throws IOException;

}
