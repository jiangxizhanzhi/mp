package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.target.TargetDto;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
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
    List<TargetModel> listTargetsByUserId(String userId);

    List<UserModel> listUsersByTargetId(String targetId);

    List<TargetModel> listMenusByUserId(String userId);

    List<TargetDto> getTreeByUserId(String userId);

    boolean bindUserTargets(String userId, List<String> targetIds, boolean cleanOldTargets);

    // 返回菜单对象集合
    List<TargetModel> listMenusByLoginUser();

    // 返回菜单对象编码集合
    List<String> listMenuIdentsByLoginUser();

    boolean deleteByUserId(String userId);

    boolean deleteByTargetId(String targetId);
}
