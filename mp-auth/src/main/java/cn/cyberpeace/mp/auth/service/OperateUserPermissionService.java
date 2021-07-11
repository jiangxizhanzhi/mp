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


@Slf4j
@Service
public class OperateUserPermissionService extends OperatePermissionService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;


    public List<String> getExcludeRoleIdsByLoginUserForSearchingUsers() {

        List<String> result = new ArrayList<>();
        result.addAll(roleService.getSystemUnVisibleRoleIds());
        boolean isSupserUser = isLoginUserSuperUser();
        if (isSupserUser) {
            
            return result;
        }

        boolean isAdmin = isLoginUserAdmin();
        if (isAdmin) {
            
            return result;
        }

        
        result.add(AuthConstant.ROLE_ADMIN_ID);
        return result;
    }

    public List<String> getExcludeUserIdsByLoginUserForSearchingUsers() {

        boolean isSupserUser = isLoginUserSuperUser();
        if (isSupserUser) {
            
            return Collections.emptyList();
        }

        
        return Arrays.asList(AuthConstant.ROOT_USER_ID);
    }

    
    public boolean isOperateSelf(List<String> targetUserIds) {
        UserModel currentLoginUser = UserUtils.getLoginUser();
        if (Objects.nonNull(currentLoginUser) && CollectionUtil.isNotEmpty(targetUserIds)) {
            String currentUserId = currentLoginUser.getId();
            return targetUserIds.contains(currentUserId);
        }

        return false;
    }


    
    public void checkByTargetUserIdsForSearching(List<String> targetUserIds) {
        if (CollectionUtil.isEmpty(targetUserIds)) {
            return;
        }

        
        
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

    
    public void checkByTargetUserIds(List<String> targetUserIds) {
        if (CollectionUtil.isEmpty(targetUserIds)) {
            return;
        }

        
        
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

    
    public void checkByTargetRoleNames(List<String> targetRoleNames) {
        if (CollectionUtil.isEmpty(targetRoleNames)) {
            return;
        }
        List<RoleModel> roleModels = roleService.listByNames(targetRoleNames);
        List<String> targetRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());
        super.checkByTargetRoleIds(targetRoleIds);
    }

}
