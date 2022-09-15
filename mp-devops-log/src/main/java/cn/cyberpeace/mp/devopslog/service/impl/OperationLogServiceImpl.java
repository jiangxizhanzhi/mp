package cn.cyberpeace.mp.devopslog.service.impl;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopslog.annotation.OperateLog;
import cn.cyberpeace.mp.devopslog.config.DevopsLogConfig;
import cn.cyberpeace.mp.devopslog.enums.LogDBEnums;
import cn.cyberpeace.mp.devopslog.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devopslog.request.OperationLogRequest;
import cn.cyberpeace.mp.devopslog.response.OperationLogPageResponse;
import cn.cyberpeace.mp.devopslog.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devopslog.service.OperationLogDbService;
import cn.cyberpeace.mp.devopslog.service.OperationLogService;
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
    private DevopsLogConfig devopsLogConfig;

    @Autowired
    private OperateLogServiceFactory operateLogServiceFactory;

    @Autowired
    private OperationLogDbService operationLogDbService;

    @Override
    public ServiceResult create(OperationLogRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String logDb = devopsLogConfig.getLogDb();
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
        String logDb = devopsLogConfig.getLogDb();
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
        String logDb = devopsLogConfig.getLogDb();
        if (LogDBEnums.ES.getCode().equals(logDb)) {
            operateLogServiceFactory.create(operateLog, request, locale);
        } else if (LogDBEnums.MYSQL.getCode().equals(logDb))  {
            operationLogDbService.create(operateLog, request, locale);
        }
    }
}
