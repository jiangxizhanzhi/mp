package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends BaseModelServiceImpl<UserModel, UserMapper, UserFields>
        implements UserService {

    @Autowired
    public UserServiceImpl(UserMapper modelMapper, UserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public ObjServiceResult<UserModel> create(UserModel obj, String[] fields) {
        obj.encodePassword();
        return super.create(obj, fields);
    }

    @Override
    public ObjServiceResult<UserModel> update(UserModel obj, String[] fields) {
        obj.encodePassword();
        return super.update(obj, fields);
    }

    @Override
    public ServiceResult<Integer> bulkCreate(List<UserModel> objs) {
        objs.forEach(UserModel::encodePassword);
        return super.bulkCreate(objs);
    }

    @Override
    public ServiceResult<Integer> bulkUpdate(List<UserModel> objs) {
        objs.forEach(UserModel::encodePassword);
        return super.bulkUpdate(objs);
    }

    @Override
    public UserModel getByUserName(String userName) {
        log.info("userName:{}", userName);
        return getModelMapper().first(new HumanCondition().filterEqual("username", userName));
    }

    @Override
    public UserModel getByMobile(String mobile) {
        log.info("mobile:{}", mobile);
        return getModelMapper().first(new HumanCondition().filterEqual("mobile", mobile));
    }

    @Override
    public boolean resetSelfPwd(String newPwd) {
        // 密码是否合法
        Assert.isFalse(PasswordUtils.isPasswordStrengthLowLevel(newPwd), AuthConstant.ERROR_CODE_PREFIX + "password_strength_invalid");

        var currentUser = UserUtils.getLoginUser();
        Assert.notNull(currentUser, AuthConstant.ERROR_CODE_PREFIX + "account_not_login");

        UserModel userUpdate = new UserModel();
        userUpdate.setId(currentUser.getId());
        userUpdate.setPassword(newPwd);
        update(userUpdate);
        return true;
    }

    @Override
    public boolean resetPwd(String userId, String newPwd) {
        // 密码是否合法
        Assert.isFalse(PasswordUtils.isPasswordStrengthLowLevel(newPwd), AuthConstant.ERROR_CODE_PREFIX + "password_strength_invalid");

        var source = get(userId).getSource();
        UserModel user = source == null ? null : source.getObj();
        Assert.notNull(user, AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");

        UserModel userUpdate = new UserModel();
        userUpdate.setId(user.getId());
        userUpdate.setPassword(newPwd);
        update(userUpdate);
        return true;
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, Map<String, Object> params) {
        Object tenant = params.getOrDefault("tenant", null);
        if (tenant != null) {
            humanCondition.filterEqual("tenant", tenant);
        }

        Object username = params.getOrDefault("username", null);
        if (username != null) {
            humanCondition.filterContains("username", username);
        }

        Object name = params.getOrDefault("name", null);
        if (name != null) {
            humanCondition.filterContains("name", name);
        }

        Object status = params.getOrDefault("status", 1);
        if (status != null) {
            humanCondition.filterEqual("status", status);
        }
    }
}
