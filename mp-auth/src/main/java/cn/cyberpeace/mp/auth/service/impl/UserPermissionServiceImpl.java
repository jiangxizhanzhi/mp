package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.auth.repository.UserPermissionMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.fields.UserPermissionFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserPermissionServiceImpl
        extends BaseModelServiceImpl<UserPermissionModel, UserPermissionMapper, UserPermissionFields>
        implements UserPermissionService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private TargetService targetService;

    @Autowired
    public UserPermissionServiceImpl(UserPermissionMapper modelMapper, UserPermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public List<TargetModel> listByUserId(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        Set<TargetModel> allTargets = new HashSet<>();


        // 用户绑定的所有角色绑定的target
        List<RoleModel> roles = userRoleService.listByUserId(userId);

        // 是否是管理员
        boolean isAdmin = roles.stream().anyMatch(r -> Objects.equals(r.getIdentifier(), "ADMIN"));
        if(isAdmin) {
            // 直接查询该租户下所有权限返回
            List<TargetModel> targets = targetService.listByTenantId("default");
            return targets;
        }

        roles.forEach((r) -> {
            List<TargetModel> targets = rolePermissionService.listTargetsByRoleId(r.getId());
            allTargets.addAll(targets);
        });

        // 用户直接绑定的target
        List<TargetModel> targets1 = getModelMapper().listByUserId(userId);
        allTargets.addAll(targets1);



        // todo 用户所在组织绑定的角色绑定的target

        // todo 用户所在组织直接绑定的target

        List<TargetModel> targetModelList = allTargets.stream().collect(Collectors.toList());
        return targetModelList;
    }

    @Override
    public List<TargetModel> listMenusByUserId(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        List<TargetModel> targets = listByUserId(userId);
        if (CollectionUtil.isNotEmpty(targets)) {
            return targets.stream().filter(t -> Objects.equals(t.getType(), TargetModel.TYPE_MENU)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
