package cn.cyberpeace.mp.cscene.core.common;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class UserCommon {

    public static UserModel getCurrentUser() {
        UserModel user = null;
        try {
            user = UserUtils.getLoginUser();
        } catch (Exception e) {
            log.error("Get current user failed: ", e);
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

    private UserCommon() {}
}
