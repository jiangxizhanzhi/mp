package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

import java.util.List;

public interface UserPermissionService extends BaseModelService<UserPermissionModel> {

    List<TargetModel> getByUserId(String userId);

    List<TargetModel> getMenusByUserId(String userId);

}
