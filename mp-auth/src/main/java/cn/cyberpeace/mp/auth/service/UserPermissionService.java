package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;


public interface UserPermissionService extends BaseModelService<UserPermissionModel, QueryParams> {

    
    List<TargetModel> listTargetsByUserId(String userId);

    List<UserModel> listUsersByTargetId(String targetId);

    List<TargetModel> listMenusByUserId(String userId);

    List<TargetBo> getTreeByUserId(String userId);

    boolean bindUserTargets(String userId, List<String> targetIds, boolean cleanOldTargets);

    
    List<TargetModel> listMenusByLoginUser();

    
    List<String> listMenuIdentsByLoginUser();

    boolean deleteByUserId(String userId);

    boolean deleteByTargetId(String targetId);
}
