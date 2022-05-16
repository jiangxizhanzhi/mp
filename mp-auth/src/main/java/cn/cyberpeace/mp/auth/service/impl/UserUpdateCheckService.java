package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserCheckService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.hutool.core.lang.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
public class UserUpdateCheckService implements UserCheckService {

    @Autowired
    private UserService userService;

    public void check(UserModel user) {
        ParamCheck.notBlank(user.getId(), AuthBasei18nCode.USER_ID);
        ObjServiceResult<UserModel> result = userService.get(user.getId());
        if (Objects.isNull(result.getSource()) || Objects.isNull(result.getSource().getObj())) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_EXISTED);
        }
        UserModel oldUser = result.getSource().getObj();

        // 修改操作允许直接修改密码
        // 密码是否合法
        if (StringUtils.isNotBlank(user.getPassword())) {
            if (PasswordUtils.isPasswordStrengthLowLevel(user.getPassword())) {
                throw new MpRuntimeException(AuthErrorCode.PASSWORD_STRENGTH_INVALID);
            }
        }

        // 邮箱是否合法
        if (StringUtils.isNotBlank(user.getEmail())) {
            if (!Validator.isEmail(user.getEmail())) {
                throw new MpRuntimeException(AuthErrorCode.EMAIL_INVALID);
            }
        }

        // 手机号是否合法

        if (StringUtils.isNotBlank(user.getUsername())) {
            // 如果username已存在，查看username是否是oldUser的
            UserModel userByUserName = userService.getByUserName(user.getUsername());

            if (userByUserName != null && !Objects.equals(userByUserName.getId(), oldUser.getId())) {
                throw new MpRuntimeException(AuthErrorCode.ACCOUNT_USERNAME_EXISTED);
            }
        }


    }


}
