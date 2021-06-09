package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.register.RegisterReq;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.AssertTrue;

@Service
@Slf4j
public class RegisterService {

    @Autowired
    private UserService userService;

    public ObjServiceResult<UserModel> register(RegisterReq registerReq) {

        UserModel userModel = new UserModel();
        userModel.setUsername(registerReq.getUsername());
        userModel.setPassword(registerReq.getPassword());
        userModel.setMobile(registerReq.getMobile());
        userModel.setEmail(registerReq.getEmail());
        userModel.setNickname(registerReq.getNickname());
        userModel.setName(registerReq.getName());

        check(userModel);

        ObjServiceResult<UserModel> result = userService.create(userModel);
        return result;
    }


    public void check(UserModel user) {
        Assert.notBlank(user.getUsername(), "username不能为空!");
//        Assert.notBlank(registerReq.getPassword(), "password不能为空!");

        // 密码是否合法
        Assert.isFalse(PasswordUtils.isPasswordStrengthLowLevel(user.getPassword()), "auth.error_code.password_strength_invalid");

        // 邮箱是否合法
        if (StringUtils.isNotBlank(user.getEmail())) {
            Assert.isTrue(Validator.isEmail(user.getEmail()), "auth.error_code.email_invalid");
        }

        // 手机号是否合法
        if (StringUtils.isNotBlank(user.getMobile())) {
            Assert.isTrue(PhoneUtil.isMobile(user.getMobile()), "auth.error_code.mobile_invalid");
            if (userService.getByMobile(user.getMobile()) != null) {
                throw new MpRuntimeException("mobile已存在！" + user.getMobile());
            }
        }

        if (userService.getByUserName(user.getUsername()) != null) {
            throw new MpRuntimeException("username已存在！" + user.getUsername());
        }

    }


}
