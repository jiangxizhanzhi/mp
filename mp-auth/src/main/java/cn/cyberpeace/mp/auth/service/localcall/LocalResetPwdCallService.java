package cn.cyberpeace.mp.auth.service.localcall;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author shgang
 * @Date 2022/11/24
 */
@Slf4j
@Service
@LocalCallServer
public class LocalResetPwdCallService {

    @Autowired
    private UserService userService;

    @LocalCallFunc
    public void resetPwd(String username, String password) {
        log.info("local-call resetPwd");
        UserModel userModel = userService.getByUserName(username);
        password = PasswordUtils.encode(password);
        UserModel newUserModel = new UserModel();
        newUserModel.setId(userModel.getId());
        newUserModel.setPassword(password);
        userService.update(newUserModel);
        log.info("local-call resetPwd success!");
    }
}
