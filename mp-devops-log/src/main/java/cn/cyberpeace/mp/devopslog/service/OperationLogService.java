package cn.cyberpeace.mp.devopslog.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopslog.annotation.OperateLog;
import cn.cyberpeace.mp.devopslog.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devopslog.request.OperationLogRequest;
import cn.cyberpeace.mp.devopslog.response.OperationLogPageResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/14
 */
public interface OperationLogService {

    ServiceResult create(OperationLogRequest request);

    ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request);

    void create(OperateLog operateLog, HttpServletRequest request);
}
