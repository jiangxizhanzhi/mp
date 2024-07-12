 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserRoleService.java.
 // File size 2683.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.role.UserRoleStatisticBo;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.request.role.UserRolePageRequest;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;

import java.util.List;

/**
 * @ClassName UserRoleService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface UserRoleService extends BaseModelService<UserRoleModel, QueryParams> {

    /**
     * listByUserId
     *
     * @param userId
     * @return
     */
    List<RoleModel> listByUserId(String userId);

    /**
     * listByUserIds
     *
     * @param userIds
     * @return
     */
    List<RoleModel> listByUserIds(List<String> userIds);

    /**
     * listUsersByRoleIdNoRestrict
     *
     * @param roleId
     * @return
     */
    List<UserModel> listUsersByRoleIdNoRestrict(String roleId);

    /**
     * pageByUserId
     *
     * @param pageRequest
     * @return
     */
    ListObjServiceResult pageByUserId(UserRolePageRequest pageRequest);

    /**
     * pageByRoleId
     *
     * @param pageRequest
     * @return
     */
    ListObjServiceResult pageByRoleId(UserRolePageRequest pageRequest);

    /**
     * 绑定一个用户，多个角色 会清除用户原来绑定的角色
     *
     * @param userId
     * @param roleIds
     * @param cleanOldRoles
     * @return boolean
     */
    boolean bindUserRoles(String userId, List<String> roleIds, boolean cleanOldRoles);

    /**
     * 绑定一个角色，多个用户
     *
     * @param roleId
     * @param userIds
     * @return boolean
     */
    boolean bindRoleUsers(String roleId, List<String> userIds);

    /**
     * 解绑一个角色，多个用户
     *
     * @param roleId
     * @param userIds
     * @return boolean
     */
    boolean deleteByRoleIdAndUserIds(String roleId, List<String> userIds);

    /**
     * deleteByUserId
     *
     * @param userId
     * @return boolean
     */
    boolean deleteByUserId(String userId);

    /**
     * deleteByRoleId
     *
     * @param roleId
     * @return boolean
     */
    boolean deleteByRoleId(String roleId);

    /**
     * 统计每个角色的用户数量
     *
     * @return
     */
    List<UserRoleStatisticBo> getUserRoleStatistics();

}
