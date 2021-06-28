package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.auth.repository.RolePermissionMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.fields.RolePermissionFields;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolePermissionServiceImpl
        extends BaseModelServiceImpl<RolePermissionModel, RolePermissionMapper, RolePermissionFields>
        implements RolePermissionService {

    @Autowired
    private RoleService roleService;

    @Autowired
    public RolePermissionServiceImpl(RolePermissionMapper modelMapper, RolePermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public List<TargetModel> listTargetsByRoleId(String roleId) {
        Assert.notBlank(roleId, "roleId不能为空！");

        List<TargetModel> targets = getModelMapper().listTargetsByRoleId(roleId);
        return targets;
    }



    @Override
    @Transactional
    public boolean bindRoleTargets(String roleId, List<String> targetIds) {
        Assert.notNull(roleId, "roleId不能为空");

        var objResult = roleService.get(roleId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            throw new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "role_not_existed");
        }

        // 清除角色绑定的权限
        boolean success = cleanRoleTargets(roleId);
        if (!success) {
            throw new MpRuntimeException("清除角色权限失败！");
        }


        if (CollectionUtil.isEmpty(targetIds)) {
            // 完成，直接返回
            return true;
        }

        // 添加角色权限
        targetIds.forEach(tid -> {
            // todo 校验权限是否存在
            RolePermissionModel rolePerm = new RolePermissionModel();
            rolePerm.setOwner(roleId);
            rolePerm.setTarget(tid);
            rolePerm.setTenant(AuthConstant.DEFAULT_TENANT);
        });

        return true;

    }

    @Override
    public boolean cleanRoleTargets(String roleId) {
        Assert.notNull(roleId, "roleId不能为空");

        var objResult = roleService.get(roleId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "role_not_existed");
        }
        getModelMapper().deleteByRole(roleId);
        return true;
    }


}
