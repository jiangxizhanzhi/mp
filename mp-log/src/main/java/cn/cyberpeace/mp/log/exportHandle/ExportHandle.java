package cn.cyberpeace.mp.log.exportHandle;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.log.bo.ExportLogBo;
import cn.cyberpeace.mp.log.bo.ExportLogEnBo;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.model.OperateLogSqlModel;
import cn.cyberpeace.mp.log.repository.OperateLogSqlMapper;
import cn.cyberpeace.mp.log.request.LogsExportrequest;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.service.impl.OperateLogSqlServiceImpl;
import com.alibaba.excel.EasyExcel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
/*
    传多个Module类型和模糊查询useNameOrOpTarget导出
 */
@Component
public class ExportHandle {

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private OperateLogSqlServiceImpl operateLogSqlService;

    @Autowired
    private OperateLogSqlMapper operateLogSqlMapper;

    public void exportExcel(HttpServletResponse response, LogsExportrequest exportRequest) throws IOException {
        List<Map<String,String>> userIdNameList = operateLogSqlMapper.selectUserIdName();
        Map<String,String> userIdNameMap = new HashMap<String,String>();
        if (CollectionUtils.isNotEmpty(userIdNameList)) {
            for (Map<String,String> temp : userIdNameList) {
                userIdNameMap.put(temp.get("id"), temp.get("name"));
            }
        }
        if (exportRequest.getModules().length >= 1){
            List<OperateLogSqlModel> list = operateLogSqlService.getModulesLogs(exportRequest.getIds(), exportRequest.getModules(), exportRequest.getType(),exportRequest.getUserNameOrOpTarget());
            Locale locale = LocaleContextHolder.getLocale();
            String sheetName = MessageCommon.get(LogConstant.EXCEL_EXPORT_NAME);
            String fname = MessageCommon.get(LogConstant.EXCEL_EXPORT_NAME);
            List dataLst = null;
            Class exportClz = null;

            if (Objects.equals(locale, Locale.US)) {
                dataLst = exportExcelEn(list,userIdNameMap);
                exportClz = ExportLogEnBo.class;
            } else {
                dataLst = exportExcel(list,userIdNameMap);
                exportClz = ExportLogBo.class;
            }

            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode(fname, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), exportClz).sheet(sheetName).doWrite(dataLst);
        }else {
            operateLogService.exportExcel(response,exportRequest);
        }

    }
    private List<ExportLogBo> exportExcel(List<OperateLogSqlModel> list, Map<String,String> userIdNameMap) {
        List<ExportLogBo> exportLogsBoList = list.stream().map(this::ExportLogs).filter(Objects::nonNull).collect(Collectors.toList());
        for(ExportLogBo bo : exportLogsBoList){
            bo.setUserRealname(userIdNameMap.get(bo.getUserId()));
        }
        return exportLogsBoList;
    }

    private List<ExportLogEnBo> exportExcelEn(List<OperateLogSqlModel> list, Map<String,String> userIdNameMap) {
        List<ExportLogEnBo> exportLogsBoList = list.stream().map(this::ExportLogsEn).filter(Objects::nonNull).collect(Collectors.toList());
        for(ExportLogEnBo bo : exportLogsBoList){
            bo.setUserRealname(userIdNameMap.get(bo.getUserId()));
        }
        return exportLogsBoList;
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
                .userId(log.getUserId())
                .module(log.getOpModule())
                .operate_type(log.getOpType())
                .operate_object(log.getOpTarget())
                .operate_time(log.getOpTime())
                .content(log.getContent()).build();
        return exportLogBo;
    }
}
