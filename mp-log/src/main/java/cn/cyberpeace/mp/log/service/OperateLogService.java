package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.model.OperateLogSqlModel;
import cn.cyberpeace.mp.log.request.LogExportRequest;
import cn.cyberpeace.mp.log.request.LogsExportrequest;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/14
 */
public interface OperateLogService {

    void create(OperationLogRequest request);

    void create(OperateLog operateLog, HttpServletRequest request);

    ServiceResult<Integer> bulkDeleteByIds(String[] ids);

    ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request);

    void  exportExcel(HttpServletResponse response, LogsExportrequest request) throws IOException;

}
