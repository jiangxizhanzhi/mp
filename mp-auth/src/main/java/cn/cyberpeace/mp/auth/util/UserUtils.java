package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    private static UserService userService;

    @SuppressWarnings({"java:S3010"})
    private UserUtils(UserService userService) {
        UserUtils.userService = userService;
    }

    public static UserModel getUser(String id) {
        UserModel user;
        // TODO 缓存获取
        ObjResult<UserModel> result = userService.get(id).getSource();
        user = result != null ? result.getObj() : null;
        return user;
    }


    /**
     * 获取当前登录用户
     * @return
     */
    public static UserModel getLoginUser() {
        UserModel user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUserDetails) {
            user = ((AuthUserDetails) principal).getUser();
        }
        return user;
    }

}
