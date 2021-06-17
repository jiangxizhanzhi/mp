package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.service.OperateLogService;
import org.springframework.stereotype.Service;

/**
 * @author wanggang
 */
@Service
public class PlatformOperateLogServiceImpl implements OperateLogService {


    @Override
    public void create(OperationLogModel operationLogModel) {

    }

    @Override
    public void search() {

    }


    @Override
    public SiteTypeEnums siteType(){
        return SiteTypeEnums.PLATFORM;
    }
}
