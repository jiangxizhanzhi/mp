package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserReportService {

    private UserService userService;

    private AuthConfig authConfig;

    private List<String> addUserReportUrls = new ArrayList<>();
    private List<String> modifyUserReportUrls = new ArrayList<>();
    private List<String> deleteUserReportUrls = new ArrayList<>();
    private List<String> logoutUserReportUrls = new ArrayList<>();

    public UserReportService(@Autowired UserService userService, @Autowired AuthConfig authConfig, @Autowired JsonCommon jsonCommon) {
        this.userService = userService;
        this.authConfig = authConfig;
        if (StringUtils.isNotEmpty(authConfig.getAuthAddReportUrl())) {
            addUserReportUrls.addAll(Arrays.asList(authConfig.getAuthAddReportUrl().split(",")));
        }
        if (StringUtils.isNotEmpty(authConfig.getAuthModifyReportUrl())) {
            modifyUserReportUrls.addAll(Arrays.asList(authConfig.getAuthModifyReportUrl().split(",")));
        }
        if (StringUtils.isNotEmpty(authConfig.getAuthDeleteReportUrl())) {
            deleteUserReportUrls.addAll(Arrays.asList(authConfig.getAuthDeleteReportUrl().split(",")));
        }
        if (StringUtils.isNotEmpty(authConfig.getAuthLogoutReportUrl())) {
            logoutUserReportUrls.addAll(Arrays.asList(authConfig.getAuthLogoutReportUrl().split(",")));
        }
        log.info("add user report url is : {}", JsonCommon.stringify(addUserReportUrls));
        log.info("modify user report url is : {}", JsonCommon.stringify(modifyUserReportUrls));
        log.info("delete user report url is : {}", JsonCommon.stringify(deleteUserReportUrls));
        log.info("logout user report url is : {}", JsonCommon.stringify(logoutUserReportUrls));
    }

    public void addUsers(List<String> userIds) {
        var userBos = this.getUserInfo(userIds);
        if (CollectionUtils.isNotEmpty(userBos)) {
            Message message = new Message();
            message.setAction(Message.ADD_ACTION);
            message.setData(userBos);
            this.doReport(message, this.addUserReportUrls);
        }
    }

    public void modifyUsers(List<String> userIds) {
        var userBos = this.getUserInfo(userIds);
        if (CollectionUtils.isNotEmpty(userBos)) {
            Message message = new Message();
            message.setAction(Message.MODIFY_ACTION);
            message.setData(userBos);
            this.doReport(message, this.modifyUserReportUrls);
        }
    }

    public void deleteUsers(List<String> userIds) {
        Message message = new Message();
        message.setAction(Message.DELETE_ACTION);
        message.setData(userIds);
        this.doReport(message, this.deleteUserReportUrls);
    }

    public void logoutUser(String user) {
        Message message = new Message();
        message.setAction(Message.LOGOUT_ACTION);
        message.setData(user);
        this.doReport(message, this.logoutUserReportUrls);
    }

    private List<UserBo> getUserInfo(List<String> userIds) {
        List<UserBo> userBos = new ArrayList<>();
        for (var user : userIds) {
            var userBo = userService.getUserDtoNoRestrict(user);
            if (userBo != null) {
                userBos.add(userBo);
            }
        }
        return userBos;
    }

    private void doReport(Message message, List<String> urls) {
        for (String url : urls) {
            TaskCommon.executeTask(() -> {
                for (int i = 0; i < 2; i++) {
                    try {
                        log.info("try to report user message at {} times");
                        var result = HttpUtils.sendPost(url, JsonCommon.stringify(message));
                        log.info("result is {}", result);
                    } catch (Exception e) {
                        log.error("failed to report user message", e);
                        continue;
                    }
                    break;
                }
            });
        }
    }

    @Getter
    @Setter
    private static class Message {
        private static String ADD_ACTION = "add";
        private static String DELETE_ACTION = "delete";
        private static String MODIFY_ACTION = "modify";
        private static String LOGOUT_ACTION = "logout";

        private String action;
        private Object data;
    }
}
