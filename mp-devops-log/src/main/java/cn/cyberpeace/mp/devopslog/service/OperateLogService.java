package cn.cyberpeace.mp.devopslog.service;

import cn.cyberpeace.mp.devopslog.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devopslog.model.OperationLogModel;
import cn.cyberpeace.mp.devopslog.request.OperationLogPageRequest;

/**
 * @author wanggang
 */
public interface OperateLogService {

    /**
     * 站点类型
     * @return
     */
    SiteTypeEnums siteType();

}
