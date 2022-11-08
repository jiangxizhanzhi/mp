package cn.cyberpeace.mp.devopslog.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.devopslog.annotation.OperateLog;
import cn.cyberpeace.mp.devopslog.model.OperationLogDbModel;
import cn.cyberpeace.mp.devopslog.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devopslog.request.OperationLogRequest;
import cn.cyberpeace.mp.devopslog.response.OperationLogPageResponse;

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
