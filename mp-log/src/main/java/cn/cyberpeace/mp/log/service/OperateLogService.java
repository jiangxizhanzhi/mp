 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateLogService.java.
 // File size 1003.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName OperateLogService
 * @Description
 * @Author shgang
 * @Date 2022/9/14
 * @Version 1.0.0
 */
public interface OperateLogService {

    /**
     * create
     *
     * @param request
     */
    void create(OperationLogRequest request);

    /**
     * create
     *
     * @param operateLog
     * @param request
     */
    void create(OperateLog operateLog, HttpServletRequest request);

    /**
     * search
     *
     * @param request
     * @return
     */
    ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request);
}
