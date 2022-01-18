package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @Description: 1. 所有通过clientid认证进来的请求，不做权限校验
 * 2. 所有经过token认证进来的请求，判断是否有对应菜单权限
 * @Author zhouxinghuai
 * @Date：2022/1/17 15:58
 */
@Component
@Slf4j
public class UserMenuPermissionUtils {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserPermissionService userPermissionService;


    public Object getAuthenticatedPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication:{}  authentication type:{}", authentication, authentication.getClass());
        Object principal = authentication.getPrincipal();

        return principal;
    }


    public boolean isClientPrincipal() {
        Object principal = getAuthenticatedPrincipal();
        if (principal instanceof ClientDetailsModel) {
            log.info("Login user is Client:{}", principal);
            return true;
        }
        return false;
    }

    /*public boolean hasUserManagePermission() {
        List<TargetModel> targets = userPermissionService.listByUserId(userId);
    }


    public boolean hasRoleManagePermission() {

    }*/



}
