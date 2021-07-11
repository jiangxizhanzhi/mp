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

        
        
        if (StringUtils.isNotBlank(user.getPassword())) {
            if (PasswordUtils.isPasswordInvalid(user.getPassword())) {
                throw new MpRuntimeException(AuthErrorCode.PASSWORD_STRENGTH_INVALID);
            }
        }

        
        if (StringUtils.isNotBlank(user.getEmail())) {
            if (!Validator.isEmail(user.getEmail())) {
                throw new MpRuntimeException(AuthErrorCode.EMAIL_INVALID);
            }
        }

        
        if (StringUtils.isNotBlank(user.getMobile())) {
            
            UserModel userByMobile = userService.getByMobile(user.getMobile());

            if (userByMobile != null && !Objects.equals(userByMobile.getId(), oldUser.getId())) {
                throw new MpRuntimeException(AuthErrorCode.ACCOUNT_MOBILE_EXISTED);
            }
        }

        if (StringUtils.isNotBlank(user.getUsername())) {
            
            UserModel userByUserName = userService.getByUserName(user.getUsername());

            if (userByUserName != null && !Objects.equals(userByUserName.getId(), oldUser.getId())) {
                throw new MpRuntimeException(AuthErrorCode.ACCOUNT_USERNAME_EXISTED);
            }
        }

        if (StringUtils.isNotBlank(user.getIdentifier())) {
            
            UserModel userByIdent = userService.getByIdent(user.getIdentifier());

            if (userByIdent != null && !Objects.equals(userByIdent.getId(), oldUser.getId())) {
                throw new MpRuntimeException(AuthErrorCode.ACCOUNT_IDENTIFIER_EXISTED);
            }
        }

    }


}
