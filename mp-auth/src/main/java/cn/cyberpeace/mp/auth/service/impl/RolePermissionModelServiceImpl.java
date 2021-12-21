package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.RolePermissionMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.fields.RolePermissionFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.collection.CollectionUtil;
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
public class RolePermissionModelServiceImpl extends BaseModelServiceImpl<RolePermissionModel, RolePermissionMapper, RolePermissionFields, QueryParams>
        implements RolePermissionModelService {

    @Autowired
    private RoleService roleService;

//    @Autowired
//    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    public RolePermissionModelServiceImpl(RolePermissionMapper modelMapper, RolePermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public List<TargetModel> listTargetsByRoleId(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);


        List<TargetModel> targets = getModelMapper().listTargetsByRoleId(roleId);
        return targets;
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
        targetIds.forEach(tid -> {
            // todo 校验权限是否存在
            RolePermissionModel rolePerm = new RolePermissionModel();
            rolePerm.setOwner(roleId);
            rolePerm.setTarget(tid);
            rolePerm.setTenant(AuthConstant.DEFAULT_TENANT);
            var re = create(rolePerm);
            ResultUtils.throwExceptionIfError(re, AuthErrorCode.ROLE_TARGET_ADD_ERROR);
        });

        return true;

    }

    @Override
    public boolean deleteByRoleId(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);

//        var result = roleService.get(roleId);
//        ResultUtils.throwExceptionIfError(result, AuthConstant.ERROR_CODE_PREFIX + "role_not_existed");

        getModelMapper().deleteByOwner(roleId);
        return true;
    }


    @Override
    public boolean deleteByTargetId(String targetId) {
        ParamCheck.notBlank(targetId, AuthBasei18nCode.TARGET_ID);
        getModelMapper().deleteByTarget(targetId);
        return true;
    }


}
