package cn.cyberpeace.mp.auth.service.localcall;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


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
