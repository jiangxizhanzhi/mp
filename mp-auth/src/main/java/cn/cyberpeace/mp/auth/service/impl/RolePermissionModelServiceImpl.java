package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.RolePermissionMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
public class RolePermissionModelServiceImpl
        implements RolePermissionModelService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<TargetModel> listTargetsByRoleId(String roleId) {
        Assert.notBlank(roleId, "roleId不能为空！");

        List<TargetModel> targets = rolePermissionMapper.listTargetsByRoleId(roleId);
        return targets;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindRoleTargets(String roleId, List<String> targetIds, boolean cleanOldTargets) {
        Assert.notNull(roleId, "roleId不能为空");

        var result = roleService.get(roleId);
        ResultUtils.throwExceptionIfError(result, AuthConstant.ERROR_CODE_PREFIX + "role_not_existed");

        if (cleanOldTargets) {
            // 清除角色绑定的权限
            boolean success = deleteByRoleId(roleId);
            if (!success) {
                throw new MpRuntimeException("清除角色权限失败！");
            }
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
            var re = create(rolePerm);
            ResultUtils.throwExceptionIfError(re, "创建角色-权限关系失败！");
        });

        return true;

    }

    @Override
    public boolean deleteByRoleId(String roleId) {
        Assert.notNull(roleId, "roleId不能为空");

//        var result = roleService.get(roleId);
//        ResultUtils.throwExceptionIfError(result, AuthConstant.ERROR_CODE_PREFIX + "role_not_existed");

        rolePermissionMapper.deleteByOwner(roleId);
        return true;
    }


    @Override
    public boolean deleteByTargetId(String targetId) {
        Assert.notNull(targetId, "targetId不能为空");
        rolePermissionMapper.deleteByTarget(targetId);
        return true;
    }


}
