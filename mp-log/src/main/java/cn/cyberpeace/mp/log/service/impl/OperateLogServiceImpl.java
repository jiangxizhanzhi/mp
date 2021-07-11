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


@Repository
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    private OperateLogDbService operateLogDbService;

    @Override
    public void create(OperationLogRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        operateLogDbService.create(request, locale);
    }

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

    @Override
    public void create(OperateLog operateLog, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        operateLogDbService.create(operateLog, request, locale);
    }
}
