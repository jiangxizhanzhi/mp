package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.bo.ExportLogBo;
import cn.cyberpeace.mp.log.bo.ExportLogEnBo;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.model.OperateLogSqlModel;
import cn.cyberpeace.mp.log.request.LogExportRequest;
import cn.cyberpeace.mp.log.request.LogsExportrequest;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.log.service.OperateLogDbService;
import cn.cyberpeace.mp.log.service.OperateLogService;
import com.alibaba.excel.EasyExcel;
import com.sun.jna.platform.unix.solaris.LibKstat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @Description
 * @Author shgang
 * @Date 2022/9/14
 */
@Slf4j
@Repository
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    private OperateLogDbService operateLogDbService;

    @Autowired
    private OperateLogSqlServiceImpl operateLogSqlServiceImpl;

    @Override
    public void create(OperationLogRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        operateLogDbService.create(request, locale);
    }

    private ExportLogEnBo ExportLogsEn(OperateLogSqlModel log) {
        ExportLogBo exportLogBo = ExportLogs(log);

        if (Objects.nonNull(exportLogBo)) {
            ExportLogEnBo exportLogEnBo = ExportLogEnBo.builder().build();
            BeanUtils.copyProperties(exportLogBo, exportLogEnBo);
            return exportLogEnBo;
        }
        return null;
    }

    private ExportLogBo ExportLogs(OperateLogSqlModel log) {
        if (Objects.isNull(log)) {
            return null;
        }
        ExportLogBo exportLogBo = ExportLogBo.builder().username(log.getUserName())
                .module(log.getOpModule())
                .operate_type(log.getOpType())
                .operate_object(log.getOpTarget())
                .operate_time(log.getOpTime())
                .content(log.getContent()).build();
        return exportLogBo;
    }

    @Override
    public ServiceResult<Integer> bulkDeleteByIds(String[] ids) {
        if (ids.length < 1) {
            log.error("delete logs , logs ids is empty");
            return new ServiceResult<>(null, BaseErrorCode.PARAMS_INVALID);
        }
        log.info("delete logs , logs ids is {}", JsonCommon.stringify(ids));
        return operateLogDbService.DeleteByIds(ids);
    }

    private List<ExportLogBo> exportExcel(List<OperateLogSqlModel> list) {
        List<ExportLogBo> exportLogsBoList = list.stream().map(this::ExportLogs).filter(Objects::nonNull).collect(Collectors.toList());
        return exportLogsBoList;
    }

    private List<ExportLogEnBo> exportExcelEn(List<OperateLogSqlModel> list) {
        List<ExportLogEnBo> exportLogsBoList = list.stream().map(this::ExportLogsEn).filter(Objects::nonNull).collect(Collectors.toList());
        return exportLogsBoList;
    }

    @Override
    public void exportExcel(HttpServletResponse response, LogsExportrequest request) throws IOException {

        List<OperateLogSqlModel> list = operateLogDbService.getLogs(request.getIds(),request.getModule(),request.getType());
        Locale locale = LocaleContextHolder.getLocale();
        String sheetName = MessageCommon.get(LogConstant.EXCEL_EXPORT_NAME);
        String fname = MessageCommon.get(LogConstant.EXCEL_EXPORT_NAME);
        List dataLst = null;
        Class exportClz = null;

        if (Objects.equals(locale, Locale.US)) {
            dataLst = exportExcelEn(list);
            exportClz = ExportLogEnBo.class;
        } else {
            dataLst = exportExcel(list);
            exportClz = ExportLogBo.class;
        }

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(fname, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), exportClz).sheet(sheetName).doWrite(dataLst);
    }

    @Override
    public void createToolLog(OperationLogRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        operateLogSqlServiceImpl.createToolLog(request, locale);
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
