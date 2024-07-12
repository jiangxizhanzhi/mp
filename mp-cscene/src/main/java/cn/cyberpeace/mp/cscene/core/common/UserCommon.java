 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserCommon.java.
 // File size 2542.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.core.common;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName UserCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class UserCommon {

    private static UserService userService;

    /**
     * getCurrentUser
     *
     * @return
     */
    public static UserModel getCurrentUser() {

        UserModel user = UserUtils.getLoginUser();
        if (Objects.isNull(user)) {
            log.error("Get current user failed!");
        }

        return user;
    }

    /**
     * getUser
     *
     * @param userId
     * @return
     */
    public static UserModel getUser(String userId) {
        UserModel user = null;
        try {
            user = UserUtils.getUser(userId);
        } catch (Exception e) {
            log.error("Get user[{}] failed: ", userId, e);
        }
        return user;
    }


    /**
     * getUsers
     *
     * @param userIds
     * @return
     */
    public static List<UserModel> getUsers(List<String> userIds) {
        return userService.getUserModelNoRestrictByUserIds(userIds);
    }

    /**
     * getUserWithContext
     *
     * @param context
     * @return
     */
    public static UserModel getUserWithContext(CsceneContextBO context) {
        if (context == null || StringUtils.isEmpty(context.getAccessUserId())) {
            return getCurrentUser();
        }

        return getUser(context.getAccessUserId());
    }

    /**
     * getUserDetail
     *
     * @param userId
     * @return
     */
    public static UserBo getUserDetail(String userId) {
        UserBo user = null;
        try {
            user = userService.getUserDtoNoRestrict(userId);
        } catch (Exception e) {
            log.error("Get user[{}] failed: ", userId, e);
        }
        return user;
    }

    /**
     * UserCommon
     *
     * @param userService
     */
    @SuppressWarnings({"java:S3010"})
    private UserCommon(UserService userService) {
        UserCommon.userService = userService;
    }
}
