package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.config.LogConfig;
import cn.cyberpeace.mp.log.enums.LogDBEnums;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.log.service.OperationLogDbService;
import cn.cyberpeace.mp.log.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/14
 */
@Repository
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private LogConfig logConfig;

    @Autowired
    private OperateLogServiceFactory operateLogServiceFactory;

    @Autowired
    private OperationLogDbService operationLogDbService;

    @Override
    public ServiceResult create(OperationLogRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String logDb = logConfig.getLogDb();
        if (LogDBEnums.ES.getCode().equals(logDb)) {
            operateLogServiceFactory.create(request, locale);
        } else if (LogDBEnums.MYSQL.getCode().equals(logDb)) {
            operationLogDbService.create(request, locale);
        }
        return ResultCommon.success();
    }

    @Override
    public ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String logDb = logConfig.getLogDb();
        ListObjServiceResult<OperationLogPageResponse> result = null;
        if (LogDBEnums.ES.getCode().equals(logDb)) {
            result = operateLogServiceFactory.search(request, locale);
        } else if (LogDBEnums.MYSQL.getCode().equals(logDb)) {
            result = operationLogDbService.search(request, locale);
        }
        return result;
    }

    @Override
    public void create(OperateLog operateLog, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String logDb = logConfig.getLogDb();
        if (LogDBEnums.ES.getCode().equals(logDb)) {
            operateLogServiceFactory.create(operateLog, request, locale);
        } else if (LogDBEnums.MYSQL.getCode().equals(logDb))  {
            operationLogDbService.create(operateLog, request, locale);
        }
    }
}
