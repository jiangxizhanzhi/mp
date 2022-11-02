package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.dto.ExportOperateLogBo;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.log.service.OperateLogDbService;
import cn.cyberpeace.mp.log.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/14
 */
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
        return result;
    }

    public List<ExportOperateLogBo> toExportBOList(List<OperationLogPageResponse> list) {

        List<ExportOperateLogBo> boList =
            list.stream().map(log -> {
                        return ExportOperateLogBo.builder()
                                .ip(log.getOpIp())
                                .username(log.getUserName())
                                .time(TimeUtils.localDateTimeToString(log.getOpTime()))
                                .build();
                    }
            ).collect(Collectors.toList());

        return boList;
    }

    @Override
    public void create(OperateLog operateLog, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        operateLogDbService.create(operateLog, request, locale);
    }
}
