package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.log.model.OperationLogModel;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;

/**
 * @ClassName MpOperateLogService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface MpOperateLogService {

    /**
     * 站点类型
     *
     * @return
     */
    SiteTypeEnums siteType();

    /**
     * 创建日志
     *
     * @param operationLogModel
     * @return
     */
    IndexResponse create(OperationLogModel operationLogModel);

    /**
     * 查询日志
     *
     * @param request
     * @return
     */
    SearchResponse search(OperationLogPageRequest request);

}
