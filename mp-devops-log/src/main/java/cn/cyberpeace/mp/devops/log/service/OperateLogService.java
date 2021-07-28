package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;

/**
 * @author wanggang
 */
public interface OperateLogService {

    /**
     * 站点类型
     * @return
     */
    SiteTypeEnums siteType();

    /**
     * 创建日志
     * @param operationLogModel
     */
    IndexResponse create(OperationLogModel operationLogModel);

    /**
     * 查询日志
     */
    SearchResponse search(OperationLogPageRequest request);

}
