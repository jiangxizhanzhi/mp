package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.log.model.OperationLogModel;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;


public interface MpOperateLogService {

    
    SiteTypeEnums siteType();

    
    IndexResponse create(OperationLogModel operationLogModel);

    
    SearchResponse search(OperationLogPageRequest request);

}
