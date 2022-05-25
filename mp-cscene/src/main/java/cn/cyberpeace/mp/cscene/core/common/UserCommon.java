package cn.cyberpeace.mp.cscene.core.common;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class UserCommon {

    private static UserService userService;

    public static UserModel getCurrentUser() {

        UserModel user = UserUtils.getLoginUser();
        if (Objects.isNull(user)) {
            log.error("Get current user failed!");
        }

        return user;
    }

    public static UserModel getUser(String userId) {
        UserModel user = null;
        try {
            user = UserUtils.getUser(userId);
        } catch (Exception e) {
            log.error("Get user[{}] failed: ", userId, e);
        }
        return user;
    }

    public static UserModel getUserWithContext(CsceneContextBO context) {
        if (context == null || StringUtils.isEmpty(context.getAccessUserId())) {
            return getCurrentUser();
        }

        return getUser(context.getAccessUserId());
    }

    public static UserDto getUserDetail(String userId) {
        UserDto user = null;
        try {
            user = userService.getUserDtoNoRestrict(userId);
        } catch (Exception e) {
            log.error("Get user[{}] failed: ", userId, e);
        }
        return user;
    }

    @SuppressWarnings({"java:S3010"})
    private UserCommon(UserService userService) {
        UserCommon.userService = userService;
    }
}
