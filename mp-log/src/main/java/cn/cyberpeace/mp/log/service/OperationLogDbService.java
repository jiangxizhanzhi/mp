package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.model.OperationLogDbModel;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/13
 */
public interface OperationLogDbService extends BaseModelService<OperationLogDbModel, QueryParams> {

    ObjServiceResult<OperationLogDbModel> create(OperationLogRequest request, Locale locale);

    ObjServiceResult<OperationLogDbModel> create(OperateLog operateLog, HttpServletRequest request, Locale locale);

    ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request, Locale locale);
}
