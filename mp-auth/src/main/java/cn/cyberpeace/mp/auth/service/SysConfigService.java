package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

public interface SysConfigService extends BaseModelService<SysConfigModel, QueryParams> {

    List<SysConfigModel> listAll();

    SysConfigModel getByKeyName(String keyName);

    boolean updateByKeyName(SysConfigModel sysConfigModel);

    boolean batchUpdateByKeyname(List<SysConfigModel> configs);

}
