 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateLogDbService.java.
 // File size 789.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

 /**
  * 操作日志db服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface OperateLogDbService {
     /**
      * 创建
      *
      * @param request 请求
      * @param locale  语言环境
      */
     void create(OperationLogRequest request, Locale locale);

     /**
      * 创建
      *
      * @param operateLog 操作日志
      * @param request    请求
      * @param locale     语言环境
      */
     void create(OperateLog operateLog, HttpServletRequest request, Locale locale);

     /**
      * 搜索
      *
      * @param request 请求
      * @param locale  语言环境
      * @return {@link ListObjServiceResult }<{@link OperationLogPageResponse }>
      */
     ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request, Locale locale);
}
