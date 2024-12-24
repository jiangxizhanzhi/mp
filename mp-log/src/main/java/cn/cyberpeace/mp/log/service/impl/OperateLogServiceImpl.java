package cn.cyberpeace.mp.log.service.impl;


import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.bo.UserModelBoNameAndId;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogExport;
import cn.cyberpeace.mp.log.response.OperationLogExportPo;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.log.service.OperateLogDbService;
import cn.cyberpeace.mp.log.service.OperateLogService;
import com.alibaba.excel.EasyExcel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
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
    public void createToolLog(OperationLogRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        request.setOpModule(1);
        operateLogDbService.createToolLog(request, locale);
    }

    @Override
    public void exportOperationLog(OperationLogPageRequest operationLogPageRequest, HttpServletResponse response) {
        operationLogPageRequest.setPage(1);
        operationLogPageRequest.setPerPage(9999);
        ListObjServiceResult<OperationLogPageResponse> search = search(operationLogPageRequest);
        //查询出审计日志信息，需要补充用户姓名
        List<OperationLogPageResponse> list = search.getSource().getList();
        List<OperationLogExportPo> operationLogExports = new ArrayList<>();
        for (OperationLogPageResponse operationLogPageResponse : list) {
            OperationLogExportPo operationLogExportPo = new OperationLogExportPo();
            BeanUtils.copyProperties(operationLogPageResponse,operationLogExportPo);
            operationLogExports.add(operationLogExportPo);
        }
        List<UserModelBoNameAndId> userModelBoNameAndIds = operateLogDbService.selectByUserId();
        for (UserModelBoNameAndId userModelBoNameAndId : userModelBoNameAndIds) {
            for (OperationLogExportPo operationLogExport : operationLogExports) {
                if (userModelBoNameAndId.getId().equals(operationLogExport.getUserId())) {
                    operationLogExport.setName(userModelBoNameAndId.getName());
                }
            }
        }
        ArrayList<OperationLogExport> exports = new ArrayList<>();
        for (OperationLogExportPo operationLogExportPo : operationLogExports) {
            OperationLogExport operationLogExport = new OperationLogExport();
            BeanUtils.copyProperties(operationLogExportPo,operationLogExport);
            exports.add(operationLogExport);
        }

        String fname = "审计日志";
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode(fname, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), OperationLogExport.class).sheet("审计日志").doWrite(exports);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(OperateLog operateLog, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        operateLogDbService.create(operateLog, request, locale);
    }
}
