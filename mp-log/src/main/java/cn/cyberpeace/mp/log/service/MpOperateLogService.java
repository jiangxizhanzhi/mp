 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MpOperateLogService.java.
 // File size 892.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
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
