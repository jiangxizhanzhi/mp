package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.TargetDto;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

import java.util.List;

public interface UserPermissionService extends BaseModelService<UserPermissionModel> {

    List<TargetModel> listByUserId(String userId);

    List<TargetModel> listMenusByUserId(String userId);

    List<TargetDto> getTreeByUserId(String userId);

}
