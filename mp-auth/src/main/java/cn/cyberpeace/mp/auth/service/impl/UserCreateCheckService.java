package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserCheckService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCreateCheckService implements UserCheckService {

    @Autowired
    private UserService userService;

    public void check(UserModel user) {
        Assert.notBlank(user.getUsername(), "username不能为空!");

        // 密码是否合法
        Assert.isFalse(PasswordUtils.isPasswordStrengthLowLevel(user.getPassword()), AuthConstant.ERROR_CODE_PREFIX + "password_strength_invalid");

        // 邮箱是否合法
        if (StringUtils.isNotBlank(user.getEmail())) {
            Assert.isTrue(Validator.isEmail(user.getEmail()), AuthConstant.ERROR_CODE_PREFIX + "email_invalid");
        }

        // 手机号是否合法
        if (StringUtils.isNotBlank(user.getMobile())) {
            Assert.isTrue(PhoneUtil.isMobile(user.getMobile()), AuthConstant.ERROR_CODE_PREFIX + "mobile_invalid");
            if (userService.getByMobile(user.getMobile()) != null) {
                throw new MpRuntimeException("该手机号码已存在！" + user.getMobile() + "，请确认用户手机号码是否正确");
            }
        }

        if (StringUtils.isNotBlank(user.getUsername())) {
            if (userService.getByUserName(user.getUsername()) != null) {
                throw new MpRuntimeException("用户名已存在！" + user.getUsername());
            }
        }

        if (StringUtils.isNotBlank(user.getIdentifier())) {
            if (userService.getByIdent(user.getIdentifier()) != null) {
                throw new MpRuntimeException("用户编号已存在！" + user.getIdentifier());
            }
        }

    }


}
