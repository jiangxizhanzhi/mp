package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.bo.target.TargetRoleBo;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.RolePermissionMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.fields.RolePermissionFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@CacheConfig(cacheManager = AuthCacheConfig.CACHE_MANAGER_NORMAL)
public class RolePermissionModelServiceImpl extends BaseModelServiceImpl<RolePermissionModel, RolePermissionMapper, RolePermissionFields, QueryParams>
        implements RolePermissionModelService {

    public static final String CACHE_TARGETS_BY_ROLES = "TARGETS_BY_ROLES";

    @Autowired
    private RoleService roleService;


    @Autowired
    public RolePermissionModelServiceImpl(RolePermissionMapper modelMapper, RolePermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Cacheable(value = CACHE_TARGETS_BY_ROLES)
    public List<TargetModel> listTargetsByRoleIds(List<String> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(AuthBasei18nCode.ROLE_ID)));
        }
        List<TargetModel> targets = getModelMapper().listTargetsByRoleIds(AuthConstant.DEFAULT_TENANT, roleIds);
        return targets;
    }

    @Override
    public List<TargetRoleBo> listTargetRoleDtosByRoleIds(List<String> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(AuthBasei18nCode.ROLE_ID)));
        }
        List<TargetRoleBo> targetRoleBos = getModelMapper().listTargetRoleDtos(roleIds);
        return targetRoleBos;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindRoleTargets(String roleId, List<String> targetIds, boolean cleanOldTargets) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);

        var result = roleService.get(roleId);
        ResultUtils.throwExceptionIfError(result, AuthErrorCode.ROLE_NOT_EXISTED);

        if (cleanOldTargets) {
            // 清除角色绑定的权限
            boolean success = deleteByRoleId(roleId);
            if (!success) {
                throw new MpRuntimeException(AuthErrorCode.ROLE_TARGET_DELETE_ERROR);
            }
        }

        if (CollectionUtil.isEmpty(targetIds)) {
            // 完成，直接返回
            return true;
        }

        // 添加角色权限
        List<RolePermissionModel> rolePerms = new ArrayList<>();
        targetIds.forEach(tid -> {
            // todo 校验权限是否存在
            RolePermissionModel rolePerm = new RolePermissionModel();
            rolePerm.setOwner(roleId);
            rolePerm.setTarget(tid);
            rolePerm.setTenant(AuthConstant.DEFAULT_TENANT);
            rolePerms.add(rolePerm);
        });

        var re = bulkCreate(rolePerms);
        return true;
    }

    @Override
    public boolean deleteByRoleId(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RolePermissionModel::getOwner, roleId);
        getModelMapper().filterDelete(humanCondition);
        return true;
    }


    @Override
    public boolean deleteByTargetId(String targetId) {
        ParamCheck.notBlank(targetId, AuthBasei18nCode.TARGET_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RolePermissionModel::getTarget, targetId);
        getModelMapper().filterDelete(humanCondition);
        return true;
    }


}
