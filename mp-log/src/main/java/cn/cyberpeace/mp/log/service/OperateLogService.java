package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;

import javax.servlet.http.HttpServletRequest;


public interface OperateLogService {

    void create(OperationLogRequest request);

    void create(OperateLog operateLog, HttpServletRequest request);

    ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request);
}
