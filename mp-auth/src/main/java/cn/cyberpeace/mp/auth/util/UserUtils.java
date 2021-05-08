package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
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
        user = userService.get(id).getSource().getObj();
        return user;
    }
}
