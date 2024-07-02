package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysModelVersionModel;
import cn.cyberpeace.mp.frontadmin.response.SysVersionResponse;

/**
 * @ClassName SysModelVersionService
 * @Description
 * @Author maojun
 * @Date 2023/8/23 15:36
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface SysModelVersionService extends BaseModelService<SysModelVersionModel, QueryParams> {

    /**
     * listSysVersion
     *
     * @return
     */
    SysVersionResponse listSysVersion();

}
