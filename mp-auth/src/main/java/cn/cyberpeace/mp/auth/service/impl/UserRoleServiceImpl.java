package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.fields.UserRoleFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImpl extends BaseModelServiceImpl<UserRoleModel, UserRoleMapper, UserRoleFields>
        implements UserRoleService {

    @Autowired
    public UserRoleServiceImpl(UserRoleMapper modelMapper, UserRoleFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public List<RoleModel> listByUserId(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        // 查询user直接绑定的角色
        List<RoleModel> roles = getModelMapper().listByUserId(userId);

        // todo 查询用户所在组织（上级组织）的角色

        return roles;
    }
}
