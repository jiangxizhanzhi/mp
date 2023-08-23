package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysModelVersionModel;

import java.util.List;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/8/23 15:36
 */
public interface SysModelVersionService extends BaseModelService<SysModelVersionModel, QueryParams> {

    List<SysModelVersionModel> listSysVersion();

}
