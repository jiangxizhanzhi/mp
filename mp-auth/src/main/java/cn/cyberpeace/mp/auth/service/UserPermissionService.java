package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.TargetDto;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

public interface UserPermissionService extends BaseModelService<UserPermissionModel, QueryParams> {

    /**
     * TYPE_ALL = 0 / null
     * TYPE_MENU = 1;
     * TYPE_API = 2;
     * TYPE_DATA = 3;
     * @param userId
     * @param targetType
     * @return
     */
    List<TargetModel> listByUserId(String userId);

    List<TargetModel> listMenusByUserId(String userId);

    List<TargetDto> getTreeByUserId(String userId);

    boolean bindUserTargets(String userId, List<String> targetIds);

    // 清除用户所有权限
    boolean cleanUserTargets(String userId);

    // 返回菜单对象集合
    List<TargetModel> listMenusByLoginUser();

    // 返回菜单对象编码集合
    List<String> listMenuIdentsByLoginUser();
}
