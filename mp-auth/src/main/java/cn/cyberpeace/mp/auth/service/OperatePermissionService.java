package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class OperatePermissionService {


    
    public boolean isLoginUserAdmin() {
        UserBo currentUserBo = UserUtils.getLoginUserDtoWithoutOrg();
        if (Objects.nonNull(currentUserBo)) {
            
            if (Objects.equals(1, currentUserBo.getIsAdmin()) || Objects.equals(1, currentUserBo.getIsSuperUser())) {
                return true;
            }
        }

        String userType = UserUtils.getLoginUserType();
        if (Objects.equals(LogConstant.USER_TYPE_CLIENT, userType)) {
            
            return true;
        }

        return false;
    }

    
    public boolean isLoginUserSuperUser() {
        UserBo currentUserBo = UserUtils.getLoginUserDtoWithoutOrg();
        if (Objects.nonNull(currentUserBo)) {
            if (Objects.equals(1, currentUserBo.getIsSuperUser())) {
                return true;
            }
        }

        String userType = UserUtils.getLoginUserType();
        if (Objects.equals(LogConstant.USER_TYPE_CLIENT, userType)) {
            
            return true;
        }

        
        return false;
    }

    
    public void checkByTargetRoleIdsForSearching(List<String> targetRoleIds) {
        if (CollectionUtil.isEmpty(targetRoleIds)) {
            return;
        }

        boolean isSupserUser = isLoginUserSuperUser();
        boolean isAdmin = isLoginUserAdmin();

        
        if (isSupserUser || isAdmin) {
            return;
        }

        UserModel currentUser = UserUtils.getLoginUser();

        
        if (hasAdminRole(targetRoleIds)) {
            if (Objects.nonNull(currentUser)) {
                log.error("permission denied! Cause target role is admin or superuser! " +
                        "current username:{}, isAdmin:{}, isSuperUser:{}", currentUser.getUsername(), isAdmin, isSupserUser);
            }
            throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
        }
    }

    
    public void checkByTargetRoleIds(List<String> targetRoleIds) {
        if (CollectionUtil.isEmpty(targetRoleIds)) {
            return;
        }

        boolean isSupserUser = isLoginUserSuperUser();
        boolean isAdmin = isLoginUserAdmin();

        
        if (isSupserUser) {
            return;
        }

        UserModel currentUser = UserUtils.getLoginUser();

        
        if (isAdmin) {
            
            
            if (hasAdminRole(targetRoleIds)) {
                if (Objects.nonNull(currentUser)) {
                    log.error("permission denied! Cause target role is admin or superuser! " +
                            "current username:{}, isAdmin:{}, isSuperUser:{}", currentUser.getUsername(), isAdmin, isSupserUser);
                }
                throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
            }
            return;
        }

        
        if (Objects.nonNull(currentUser)) {
            log.error("permission denied for normal user! " +
                    "current username:{}, isAdmin:{}, isSuperUser:{}", currentUser.getUsername(), isAdmin, isSupserUser);
        }
        throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);

    }


    
    private boolean hasAdminRole(List<String> roleIds) {
        return roleIds.stream()
                .filter(r -> Objects.equals(r.trim(), AuthConstant.ROLE_ADMIN_ID))
                .findAny().isPresent();
    }

}
