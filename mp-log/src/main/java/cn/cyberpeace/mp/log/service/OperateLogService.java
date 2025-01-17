 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateLogService.java.
 // File size 717.
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

 /**
  * 操作日志服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface OperateLogService {

     /**
      * 创建
      *
      * @param request 请求
      */
     void create(OperationLogRequest request);

     /**
      * 创建
      *
      * @param operateLog 操作日志
      * @param request    请求
      */
     void create(OperateLog operateLog, HttpServletRequest request);

     /**
      * 搜索
      *
      * @param request 请求
      * @return {@link ListObjServiceResult }<{@link OperationLogPageResponse }>
      */
     ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request);
}
