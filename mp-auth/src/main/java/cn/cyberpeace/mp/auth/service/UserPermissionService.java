package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

/**
 * @ClassName UserPermissionService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface UserPermissionService extends BaseModelService<UserPermissionModel, QueryParams> {

    /**
     * TYPE_ALL = 0 / null
     * TYPE_MENU = 1;
     * TYPE_API = 2;
     * TYPE_DATA = 3;
     * @param userId
     * @return
     */
    List<TargetModel> listTargetsByUserId(String userId);

    /**
     * listMenusByUserId
     *
     * @param userId
     * @return
     */
    List<TargetModel> listMenusByUserId(String userId);

    /**
     * getTreeByUserId
     *
     * @param userId
     * @return
     */
    List<TargetBo> getTreeByUserId(String userId);

    /**
     * bindUserTargets
     *
     * @param userId
     * @param targetIds
     * @param cleanOldTargets
     * @return boolean
     */
    boolean bindUserTargets(String userId, List<String> targetIds, boolean cleanOldTargets);

    /**
     * listMenusByLoginUser
     *
     * @return
     */// 返回菜单对象集合
    List<TargetModel> listMenusByLoginUser();

    /**
     * listMenuIdentsByLoginUser
     *
     * @return
     */// 返回菜单对象编码集合
    List<String> listMenuIdentsByLoginUser();

    /**
     * deleteByUserId
     *
     * @param userId
     * @return boolean
     */
    boolean deleteByUserId(String userId);

    /**
     * deleteByTargetId
     *
     * @param targetId
     * @return boolean
     */
    boolean deleteByTargetId(String targetId);
}
