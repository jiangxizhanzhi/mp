package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserRoleFields;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleServiceImpl extends BaseModelServiceImpl<UserRoleModel, UserRoleMapper, UserRoleFields>
        implements UserRoleService {

    @Autowired
    private UserService userService;

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

        // todo 查询用户所在组织（上级组织）的角色  暂无
        return roles;
    }


    @Override
    @Transactional
    public boolean bindUserRoles(String userId, List<String> roleIds) {
        Assert.notNull(userId, "userId不能为空");

        var objResult = userService.get(userId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            throw new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");
        }

        // 清除用户角色
        boolean success = cleanUserRoles(userId);
        if (!success) {
            throw new MpRuntimeException("清除用户角色失败！");
        }

        if (CollectionUtil.isEmpty(roleIds)) {
            // 完成，直接返回
            return true;
        }

        // 添加用户角色
        roleIds.forEach(rid -> {
            // todo 校验角色是否存在
            UserRoleModel userRole = new UserRoleModel();
            userRole.setUser(userId);
            userRole.setRole(rid);
            userRole.setTenant(AuthConstant.DEFAULT_TENANT);
            create(userRole);
        });

        return true;
    }

    @Override
    public boolean cleanUserRoles(String userId) {
        Assert.notNull(userId, "userid不能为空");

        var objResult = userService.get(userId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");
        }

        getModelMapper().deleteByUser(userId);

        return true;
    }


}
