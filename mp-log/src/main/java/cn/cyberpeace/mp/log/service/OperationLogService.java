package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;

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
