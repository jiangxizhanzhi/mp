 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateLogServiceImpl.java.
 // File size 2234.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.log.service.OperateLogDbService;
import cn.cyberpeace.mp.log.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

 /**
  * 操作日志服务impl
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Repository
public class OperateLogServiceImpl implements OperateLogService {

     /**
      * 操作日志db服务
      */
     @Autowired
    private OperateLogDbService operateLogDbService;

     /**
      * 创建
      *
      * @param request 请求
      */
     @Override
    public void create(OperationLogRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        operateLogDbService.create(request, locale);
    }

     /**
      * 搜索
      *
      * @param request 请求
      * @return {@link ListObjServiceResult }<{@link OperationLogPageResponse }>
      */
     @Override
    public ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        ListObjServiceResult<OperationLogPageResponse> result = operateLogDbService.search(request, locale);
        var logList = result.getSource().getObjs();
        LocalDateTime now = LocalDateTime.now();
        logList.forEach(
                log -> {
                    LocalDateTime opTime = log.getOpTime();
                    var seconds = Duration.between(opTime, now).getSeconds();
                    seconds = seconds <= 0 ? 1 : seconds;
                    var millSeconds = seconds * 1000;
                    log.setOpMillSecondsBefore(millSeconds);
                }
        );
        return result;
    }

     /**
      * 创建
      *
      * @param operateLog 操作日志
      * @param request    请求
      */
     @Override
    public void create(OperateLog operateLog, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        operateLogDbService.create(operateLog, request, locale);
    }
}
