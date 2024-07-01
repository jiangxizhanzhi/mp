package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @Description
 * @Author shgang
 * @Date 2022/10/12
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
