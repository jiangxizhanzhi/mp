package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserCheckService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.lang.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
public class UserCreateCheckService implements UserCheckService {

    @Autowired
    private UserService userService;

    public void check(UserModel user) {

        ParamCheck.notBlank(user.getUsername(), AuthBasei18nCode.USER_USERNAME);

        // 密码是否合法
//        if (PasswordUtils.isPasswordStrengthLowLevel(user.getPassword())) {
//            throw new MpRuntimeException(AuthErrorCode.PASSWORD_STRENGTH_INVALID);
//        }

        // 邮箱是否合法
//        if (StringUtils.isNotBlank(user.getEmail())) {
//            if (!Validator.isEmail(user.getEmail())) {
//                throw new MpRuntimeException(AuthErrorCode.EMAIL_INVALID);
//            }
//        }

        // 手机号是否合法
//        if (StringUtils.isNotBlank(user.getMobile())) {
//
//            /*
//            // todo 国际化手机号校验
//            if (!PhoneUtil.isMobile(user.getMobile())) {
//                throw new MpRuntimeException(AuthErrorCode.MOBILE_INVALID);
//            }*/
//
//            if (userService.getByMobile(user.getMobile()) != null) {
//                throw new MpRuntimeException(AuthErrorCode.ACCOUNT_MOBILE_EXISTED);
//            }
//        }

        if (StringUtils.isNotBlank(user.getUsername())) {
            if (userService.getByUserName(user.getUsername()) != null) {
                throw new MpRuntimeException(AuthErrorCode.ACCOUNT_USERNAME_EXISTED);
            }
        }

//        if (StringUtils.isNotBlank(user.getIdentifier())) {
//            if (userService.getByIdent(user.getIdentifier()) != null) {
//                throw new MpRuntimeException(AuthErrorCode.ACCOUNT_IDENTIFIER_EXISTED);
//            }
//        }

    }


}
