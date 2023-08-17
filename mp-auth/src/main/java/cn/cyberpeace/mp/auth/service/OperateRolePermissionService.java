package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Description: 查看当前用户，是否有权对角色进行增删改查
 * @Author zhouxinghuai
 * @Date：2022/05/12 11:15
 */
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
}
