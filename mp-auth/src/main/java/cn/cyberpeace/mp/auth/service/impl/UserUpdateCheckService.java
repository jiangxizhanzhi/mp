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
import com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer;
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

        // 修改操作不允许直接修改密码
        user.setPassword(null);

        // 邮箱是否合法
        if (StringUtils.isNotBlank(user.getEmail())) {
            Assert.isTrue(Validator.isEmail(user.getEmail()), AuthConstant.ERROR_CODE_PREFIX + "email_invalid");
        }

        // 手机号是否合法
        if (StringUtils.isNotBlank(user.getMobile())) {
            Assert.isTrue(PhoneUtil.isMobile(user.getMobile()), AuthConstant.ERROR_CODE_PREFIX + "mobile_invalid");
            // 如果手机号已存在，查看手机号是否是oldUser
            UserModel userByMobile = userService.getByMobile(user.getMobile());

            if (userByMobile != null && !Objects.equals(userByMobile.getId(), oldUser.getId())) {
                throw new MpRuntimeException("该手机号码已存在！" + user.getMobile() + "，请确认用户手机号码是否正确");
            }
        }

        if (StringUtils.isNotBlank(user.getUsername())) {
            // 如果username已存在，查看username是否是oldUser的
            UserModel userByUserName = userService.getByUserName(user.getUsername());

            if (userByUserName != null && !Objects.equals(userByUserName.getId(), oldUser.getId())) {
                throw new MpRuntimeException("用户名已存在！" + user.getUsername());
            }
        }

        if (StringUtils.isNotBlank(user.getIdentifier())) {
            // 如果identifier已存在，查看identifier是否是oldUser的
            UserModel userByIdent = userService.getByIdent(user.getIdentifier());

            if (userByIdent != null && !Objects.equals(userByIdent.getId(), oldUser.getId())) {
                throw new MpRuntimeException("用户编号已存在！" + user.getIdentifier());
            }
        }

    }


}
