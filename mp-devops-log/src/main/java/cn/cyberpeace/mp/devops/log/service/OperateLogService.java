package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;

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
    Object create(OperationLogModel operationLogModel);

    /**
     * 查询日志
     */
    void search(OperationLogPageRequest request);

}
