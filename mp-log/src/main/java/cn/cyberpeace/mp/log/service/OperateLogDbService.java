package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.model.OperateLogSqlModel;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.List;


/**
 * @Description
 * @Author shgang
 * @Date 2022/10/12
 */
public interface OperateLogDbService {
    void create(OperationLogRequest request, Locale locale);

    void create(OperateLog operateLog, HttpServletRequest request, Locale locale);

    ServiceResult<Integer> DeleteByIds(String[] ids);

    ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request, Locale locale);

    List<OperateLogSqlModel> getLogs(String[] ids, Integer module, Integer type);

}
