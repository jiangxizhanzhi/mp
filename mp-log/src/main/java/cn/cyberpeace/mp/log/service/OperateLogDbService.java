 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateLogDbService.java.
 // File size 1143.
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
import java.util.Locale;

/**
 * @ClassName OperateLogDbService
 * @Description
 * @Author shgang
 * @Date 2022/10/12
 * @Version 1.0.0
 */
public interface OperateLogDbService {
    /**
     * create
     *
     * @param request
     * @param locale
     */
    void create(OperationLogRequest request, Locale locale);

    /**
     * create
     *
     * @param operateLog
     * @param request
     * @param locale
     */
    void create(OperateLog operateLog, HttpServletRequest request, Locale locale);

    /**
     * search
     *
     * @param request
     * @param locale
     * @return
     */
    ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request, Locale locale);
}
