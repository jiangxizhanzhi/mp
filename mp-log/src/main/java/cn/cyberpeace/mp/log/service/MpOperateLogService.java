 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MpOperateLogService.java.
 // File size 723.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.log.model.OperationLogModel;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;

/**
 * @author wanggang
 */
public interface MpOperateLogService {

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
