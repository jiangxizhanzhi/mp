 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateUserPermissionService.java.
 // File size 5671.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

 /**
  * 操作用户许可服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Service
public class OperateUserPermissionService extends OperatePermissionService {

     /**
      * 用户服务
      */
     @Autowired
    private UserService userService;

     /**
      * 角色服务
      */
     @Autowired
    private RoleService roleService;

     /**
      * 用户角色服务
      */
     @Autowired
    private UserRoleService userRoleService;

     /**
      * 通过登录用户获取排除角色id以搜索用户
      *
      * @return {@link List }<{@link String }>
      */
     public List<String> getExcludeRoleIdsByLoginUserForSearchingUsers() {

        List<String> result = new ArrayList<>();
        result.addAll(roleService.getSystemUnVisibleRoleIds());
        boolean isSupserUser = isLoginUserSuperUser();
        if (isSupserUser) {
            // 超级用户不受限制
            return result;
        }

        boolean isAdmin = isLoginUserAdmin();
        if (isAdmin) {
            // 管理员可以查询同级管理员用户，但不能查询超级用户
            return result;
        }

        // 普通用户不可查询管理员和超级用户
        result.add(AuthConstant.ROLE_ADMIN_ID);
        return result;
    }

     /**
      * 根据登录用户获取排除用户id，用于搜索用户
      *
      * @return {@link List }<{@link String }>
      */
     public List<String> getExcludeUserIdsByLoginUserForSearchingUsers() {

        boolean isSupserUser = isLoginUserSuperUser();
        if (isSupserUser) {
            // 超级用户不受限制
            return Collections.emptyList();
        }

        // 其他角色用户不允许查询超级用户
        return Arrays.asList(AuthConstant.ROOT_USER_ID);
    }

     /**
      * targetUserId是否是登录用户自身
      *
      * @param targetUserIds 目标用户id
      * @return
      */
     public boolean isOperateSelf(List<String> targetUserIds) {
        UserModel currentLoginUser = UserUtils.getLoginUser();
        if (Objects.nonNull(currentLoginUser) && CollectionUtil.isNotEmpty(targetUserIds)) {
            String currentUserId = currentLoginUser.getId();
            return targetUserIds.contains(currentUserId);
        }

        return false;
    }

     /**
      * 根据操作用户id集合进行权限校验
      * 如：批量删除用户，批量更新用户等
      *
      * @param targetUserIds 目标用户id
      */
     public void checkByTargetUserIdsForSearching(List<String> targetUserIds) {
        if (CollectionUtil.isEmpty(targetUserIds)) {
            return;
        }

        // 自己可以查自己
        // 去重
        List<String> distinctUIds = targetUserIds.stream().distinct().collect(Collectors.toList());
        if (distinctUIds.size() == 1) {
            UserModel currentLoginUser = UserUtils.getLoginUser();
            if (Objects.nonNull(currentLoginUser) && Objects.equals(currentLoginUser.getId(), targetUserIds.get(0))) {
                return;
            }
        }

        boolean isSupserUser = isLoginUserSuperUser();
        if (!isSupserUser) {
            if (targetUserIds.contains(AuthConstant.ROOT_USER_ID)) {
                log.error("Admin or normal role user cannot fetch root user info...");
                throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
            }
        }

        List<RoleModel> roleModels = userRoleService.listByUserIds(targetUserIds);
        List<String> targetRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());
        super.checkByTargetRoleIdsForSearching(targetRoleIds);
    }

     /**
      * 根据操作用户id集合进行权限校验
      * 如：批量删除用户，批量更新用户等
      *
      * @param targetUserIds 目标用户id
      */
     public void checkByTargetUserIds(List<String> targetUserIds) {
        if (CollectionUtil.isEmpty(targetUserIds)) {
            return;
        }

        // 自己可以查自己
        // 去重
        List<String> distinctUIds = targetUserIds.stream().distinct().collect(Collectors.toList());
        if (distinctUIds.size() == 1) {
            UserModel currentLoginUser = UserUtils.getLoginUser();
            if (Objects.nonNull(currentLoginUser) && Objects.equals(currentLoginUser.getId(), targetUserIds.get(0))) {
                return;
            }
        }

        List<RoleModel> roleModels = userRoleService.listByUserIds(targetUserIds);
        List<String> targetRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());
        super.checkByTargetRoleIds(targetRoleIds);
    }

     /**
      * 根据操作对象的所有角色名称检查权限
      * 如：excel批量导入
      *
      * @param targetRoleNames 目标角色名称
      */
     public void checkByTargetRoleNames(List<String> targetRoleNames) {
        if (CollectionUtil.isEmpty(targetRoleNames)) {
            return;
        }
        List<RoleModel> roleModels = roleService.listByNames(targetRoleNames);
        List<String> targetRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());
        super.checkByTargetRoleIds(targetRoleIds);
    }

}
