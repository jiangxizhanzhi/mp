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


@Slf4j
@Service
public class OperateRolePermissionService extends OperatePermissionService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;


    public List<String> getExcludeRoleIdsByLoginUserForSearchingRoles() {

        boolean isSupserUser = isLoginUserSuperUser();
        if (isSupserUser) {
            
            return Collections.emptyList();
        }

        boolean isAdmin = isLoginUserAdmin();
        if (isAdmin) {
            
            return Arrays.asList(AuthConstant.ROLE_ADMIN_ID);
        }

        
        return Arrays.asList(AuthConstant.ROLE_ADMIN_ID);
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
