package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

import java.util.List;

public interface RoleService extends BaseModelService<RoleModel> {

    List<RoleModel> getByTenant(String tenant);

}
