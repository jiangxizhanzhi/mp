 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResetPwdLocalCallService.java.
 // File size 1234.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service.localcall;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description
 * @Author shgang
 * @Date 2022/11/24
 */
@Slf4j
@Service
@LocalCallServer
public class ResetPwdLocalCallService {

    @Autowired
    private UserService userService;

    @LocalCallFunc
    public void resetPwd(String username, String password) {
        log.info("local-call resetPwd");
        UserModel userModel = userService.getByUserName(username);
        if (Objects.isNull(userModel)) {
            log.error("failed to reset password, username: {}", username);
            return;
        }
        UserModel newUserModel = new UserModel();
        newUserModel.setId(userModel.getId());
        newUserModel.setPassword(password);
        userService.update(newUserModel);
        log.info("local-call resetPwd success!");
    }
}
