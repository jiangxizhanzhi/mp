package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserCheckService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserUpdateCheckService implements UserCheckService {

    @Autowired
    private UserService userService;

    public void check(UserModel user) {
        Assert.notBlank(user.getId(), "用户id不能为空!");
        ObjServiceResult<UserModel> result = userService.get(user.getId());
        if (Objects.isNull(result.getSource()) || Objects.isNull(result.getSource().getObj())) {
            throw new MpRuntimeException("用户不存在！ id:" + user.getId());
        }
        UserModel oldUser = result.getSource().getObj();

        // 修改操作允许直接修改密码
        // 密码是否合法
        if (StringUtils.isNotBlank(user.getPassword())) {
            Assert.isFalse(PasswordUtils.isPasswordStrengthLowLevel(user.getPassword()), AuthConstant.ERROR_CODE_PREFIX + "password_strength_invalid");
        }

        // 邮箱是否合法
        if (StringUtils.isNotBlank(user.getEmail())) {
            Assert.isTrue(Validator.isEmail(user.getEmail()), AuthConstant.ERROR_CODE_PREFIX + "email_invalid");
        }

        if (StringUtils.isNotBlank(user.getUsername())) {
            // 如果username已存在，查看username是否是oldUser的
            UserModel userByUserName = userService.getByUserName(user.getUsername());

            if (userByUserName != null && !Objects.equals(userByUserName.getId(), oldUser.getId())) {
                throw new MpRuntimeException("用户名已存在！" + user.getUsername() + "，请确认用户名是否正确");
            }
        }


    }


}
