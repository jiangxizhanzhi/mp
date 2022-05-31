package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/13 11:57
 */
@Slf4j
@Service
public class OperatePermissionService {


    /**
     * 登录用户是否是普通管理员
     * client认证的用户都有管理员权限
     * @return
     */
    public boolean isLoginUserAdmin() {
        UserDto currentUserDto = UserUtils.getLoginUserDtoWithoutOrg();
        if (Objects.nonNull(currentUserDto)) {
            // 超级用户拥有管理员权限
            if (Objects.equals(1, currentUserDto.getIsAdmin()) || Objects.equals(1, currentUserDto.getIsSuperUser())) {
                return true;
            }
        }

        String userType = UserUtils.getLoginUserType();
        if (Objects.equals(LogConstant.USER_TYPE_CLIENT, userType)) {
            // client类型的认证，默认拥有管理员，超级用户权限
            return true;
        }

        return false;
    }

    /**
     * 登录用户是否是超级用户
     * client认证的用户都有超级用户权限
     * @return
     */
    public boolean isLoginUserSuperUser() {
        UserDto currentUserDto = UserUtils.getLoginUserDtoWithoutOrg();
        if (Objects.nonNull(currentUserDto)) {
            if (Objects.equals(1, currentUserDto.getIsSuperUser())) {
                return true;
            }
        }

        String userType = UserUtils.getLoginUserType();
        if (Objects.equals(LogConstant.USER_TYPE_CLIENT, userType)) {
            // client类型的认证，默认拥有管理员，超级用户权限
            return true;
        }

        // client认证的用户都有超级用户权限
        return false;
    }

    /**
     * 根据操作对象的所有角色id集合检查权限
     * @param targetRoleIds
     */
    public void checkByTargetRoleIds(List<String> targetRoleIds) {
        if (CollectionUtil.isEmpty(targetRoleIds)) {
            return;
        }

        boolean isSupserUser = isLoginUserSuperUser();
        boolean isAdmin = isLoginUserAdmin();

        // 超级用户不限制
        if (isSupserUser) {
            return;
        }

        UserModel currentUser = UserUtils.getLoginUser();

        // 自身是非超级用户的管理员
        if (isAdmin) {
            // 管理员不可操作拥有管理员，超级用户角色的账号，但是可以操作普通用户
            // 管理员甚至无权修改自己的信息
            if (hasAdminRole(targetRoleIds)) {
                if (Objects.nonNull(currentUser)) {
                    log.error("permission denied! Cause target role is admin or superuser! " +
                            "current username:{}, isAdmin:{}, isSuperUser:{}", currentUser.getUsername(), isAdmin, isSupserUser);
                }
                throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
            }
            return;
        }

        // 普通用户不允许操作
        if (Objects.nonNull(currentUser)) {
            log.error("permission denied for normal user! " +
                    "current username:{}, isAdmin:{}, isSuperUser:{}", currentUser.getUsername(), isAdmin, isSupserUser);
        }
        throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);

    }



    public List<String> getExcludeRoleIdsByLoginUserForSearching() {

        boolean isSupserUser = isLoginUserSuperUser();
        if (isSupserUser) {
            // 超级用户不受限制
            return Collections.emptyList();
        }

        boolean isAdmin = isLoginUserAdmin();
        if (isAdmin) {
            // 管理员不可查询同级管理员和超级用户
            return Arrays.asList(AuthConstant.ROLE_ADMIN_ID);
        }

        // 普通用户不可查询管理员和超级用户
        return Arrays.asList(AuthConstant.ROLE_ADMIN_ID);
    }


    /**
     * 判断角色id集合中有无管理员
     * @param roleIds
     * @return
     */
    private boolean hasAdminRole(List<String> roleIds) {
        return roleIds.stream()
                .filter(r -> Objects.equals(r.trim(), AuthConstant.ROLE_ADMIN_ID))
                .findAny().isPresent();
    }

}
