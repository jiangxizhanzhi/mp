 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ReportLogFileServiceImpl.java.
 // File size 1242.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
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

 /**
  * MP中台日志模块
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
@Slf4j
@RequiredArgsConstructor
public class ReportLogFileServiceImpl implements LogFileService {

    private final LogReportService logReportService;

     /**
      * 日志报表类型
      *
      * @return {@link String }
      */
     @Override
    public String logReportType() {
        return LogReportTypeEnum.REPORT.toValue();
    }

     /**
      * 列表
      *
      * @param type 类型
      * @return {@link List }<{@link LogReportDTO }>
      */
     @Override
    public List<LogReportDTO> list(String type) {
        return logReportService.list(type);
    }

     /**
      * 下载
      *
      * @param downloadDTO 下载dto
      * @return {@link LogFileDownloadBO }
      * @throws IOException ioexception
      */
     @Override
    public LogFileDownloadBO download(LogDownloadDTO downloadDTO) throws IOException{
        return null;
    }

     /**
      * 得到订单
      *
      * @return int
      */
     @Override
    public int getOrder() {
        return 20;
    }
}
