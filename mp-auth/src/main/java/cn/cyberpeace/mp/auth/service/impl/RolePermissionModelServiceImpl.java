package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.target.TargetRoleBo;
import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.RolePermissionMapper;
import cn.cyberpeace.mp.auth.repository.TargetMapper;
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
import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


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

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private TargetMapper targetMapper;

    @Override
    @Cacheable(value = CACHE_TARGETS_BY_ROLES)
    public List<TargetModel> listTargetsByRoleIds(List<String> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(AuthBasei18nCode.ROLE_ID)));
        }
        HumanCondition rolePermissionCondition = new HumanCondition()
                .filterEqual(RolePermissionModel::getTenant, AuthConstant.DEFAULT_TENANT)
                .filterIn(RolePermissionModel::getOwner, roleIds.toArray())
                .select(RolePermissionModel.QUERY_FIELD_TARGET);
        List<RolePermissionModel> rolePermissionModels = rolePermissionMapper.list(rolePermissionCondition);
        List<String> ids = new ArrayList<>();
        rolePermissionModels.forEach(model -> ids.add(model.getTarget()));
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        HumanCondition targetCondition = new HumanCondition()
                .filterIn(TargetModel::getId, ids.toArray())
                .filterEqual(TargetModel::getVisible, 1)
                .distinct();
        List<TargetModel> targetModels = targetMapper.list(targetCondition);
        return targetModels;
    }

    @Override
    public List<TargetRoleBo> listTargetRoleDtosByRoleIds(List<String> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(AuthBasei18nCode.ROLE_ID)));
        }
        HumanCondition condition = new HumanCondition()
                .link(RolePermissionModel.class)
                .link(RoleModel.class)
                .filterLink(RoleModel.class, RoleModel::getId, RolePermissionModel.class, RolePermissionModel::getOwner)
                .link(TargetModel.class)
                .filterLink(TargetModel.class, TargetModel::getId, RolePermissionModel.class, RolePermissionModel::getTarget)
                .currentLink(RoleModel.class)
                .filterIn(RoleModel::getId, roleIds.toArray())
                .selectAs(RoleModel::getId, TargetRoleBo.QUERY_FIELD_ROLE_ID)
                .currentLink(TargetModel.class)
                .selectAs(TargetModel::getId, TargetRoleBo.QUERY_FIELD_ID)
                .selectAs(TargetModel::getName, TargetRoleBo.QUERY_FIELD_NAME)
                .selectAs(TargetModel::getPk, TargetRoleBo.QUERY_FIELD_PK)
                .selectAs(TargetModel::getParent, TargetRoleBo.QUERY_FIELD_PARENT)
                .selectAs(TargetModel::getPath, TargetRoleBo.QUERY_FIELD_PATH)
                .selectAs(TargetModel::getVisible, TargetRoleBo.QUERY_FIELD_VISIBLE)
                .selectAs(TargetModel::getTenant, TargetRoleBo.QUERY_FIELD_TENANT)
                .selectAs(TargetModel::getIdentifier, TargetRoleBo.QUERY_FIELD_IDENTIFIER)
                .selectAs(TargetModel::getType, TargetRoleBo.QUERY_FIELD_TYPE)
                .selectAs(TargetModel::getCreateTime, TargetRoleBo.QUERY_FIELD_CREATE_TIME)
                .selectAs(TargetModel::getUpdateTime, TargetRoleBo.QUERY_FIELD_UPDATE_TIME);
        List<TargetRoleBo> targetRoles = rolePermissionMapper.list(condition, TargetRoleBo.class);
        return targetRoles;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindRoleTargets(String roleId, List<String> targetIds, boolean cleanOldTargets) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);

        var result = roleService.get(roleId);
        ResultUtils.throwExceptionIfError(result, AuthErrorCode.ROLE_NOT_EXISTED);

        if (cleanOldTargets) {
            
            boolean success = deleteByRoleId(roleId);
            if (!success) {
                throw new MpRuntimeException(AuthErrorCode.ROLE_TARGET_DELETE_ERROR);
            }
        }

        if (CollUtil.isEmpty(targetIds)) {
            
            return true;
        }

        
        List<RolePermissionModel> rolePerms = new ArrayList<>();
        targetIds.forEach(tid -> {
            
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
