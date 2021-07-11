package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;


public interface SysActivCtlService extends BaseModelService<SysActivCtlModel, QueryParams> {

    SysActivCtlModel selectValid();

    SysActivCtlModel selectUnused();

    
    void invalidOtherExceptOne(String exceptedId);

}
