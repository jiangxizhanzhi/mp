 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateRolePermissionService.java.
 // File size 2054.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

 /**
  * 操作角色许可服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Service
public class OperateRolePermissionService extends OperatePermissionService {

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
      * 通过登录用户获取排除角色id，用于搜索角色
      *
      * @return {@link List }<{@link String }>
      */
     public List<String> getExcludeRoleIdsByLoginUserForSearchingRoles() {

        boolean isSupserUser = isLoginUserSuperUser();
        if (isSupserUser) {
            // 超级用户不受限制
            return Collections.emptyList();
        }

        boolean isAdmin = isLoginUserAdmin();
        if (isAdmin) {
            // 管理员不可查询同级管理员角色和超级用户角色
            return Arrays.asList(AuthConstant.ROLE_ADMIN_ID);
        }

        // 普通用户不可查询管理员和超级用户
        return Arrays.asList(AuthConstant.ROLE_ADMIN_ID);
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
