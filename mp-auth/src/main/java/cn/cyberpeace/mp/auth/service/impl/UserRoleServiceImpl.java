package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.role.UserRolePageRequest;
import cn.cyberpeace.mp.auth.dto.role.UserRoleStatistic;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserRoleFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@CacheConfig(cacheManager = AuthCacheConfig.CACHE_MANAGER_45S)
public class UserRoleServiceImpl extends BaseModelServiceImpl<UserRoleModel, UserRoleMapper, UserRoleFields, QueryParams>
        implements UserRoleService {

    public static final String CACHE_VAL_ROLES= "USER_ROLES_CACHE";


    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private OperateUserPermissionService opUserPermService;

    @Autowired
    public UserRoleServiceImpl(UserRoleMapper modelMapper, UserRoleFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Cacheable(value = CACHE_VAL_ROLES)
    public List<RoleModel> listByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        List<RoleModel> roles = getModelMapper().listRolesByUserId(userId);
        return roles;
    }


    @Override
    @Cacheable(value = CACHE_VAL_ROLES)
    public List<RoleModel> listByUserIds(List<String> userIds) {
        Assert.notEmpty(userIds, "userIds不能为空！");
        List<RoleModel> roles = getModelMapper().listRolesByUserIds(userIds);

        return roles;
    }

    @Override
    public List<UserModel> listUsersByRoleIdNoRestrict(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);
        List<UserModel> users = getModelMapper().listUsersByRoleId(roleId);
        return users;
    }

    @Override
    public PageInfo pageByUserId(UserRolePageRequest pageRequest) {
        pageRequest.ensure();
        ParamCheck.notBlank(pageRequest.getUserId(), AuthBasei18nCode.USER_ID);
        opUserPermService.checkByTargetUserIds(Arrays.asList(pageRequest.getUserId()));
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getPageSize());
        // 查询user直接绑定的角色
        List<RoleModel> roles = getModelMapper().listRolesByUserId(pageRequest.getUserId());
        PageInfo pageInfo = new PageInfo(roles);
        // todo 查询用户所在组织（上级组织）的角色  暂无此功能
        return pageInfo;
    }

    @Override
    public PageInfo pageByRoleId(UserRolePageRequest pageRequest) {
        pageRequest.ensure();
        ParamCheck.notBlank(pageRequest.getRoleId(), AuthBasei18nCode.ROLE_ID);
        List<String> excludeRoleIds = opUserPermService.getExcludeRoleIdsByLoginUserForSearching();
        if (excludeRoleIds.contains(pageRequest.getRoleId())) {
            new PageInfo<>(Collections.emptyList());
        }
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getPageSize());
        List<UserModel> userModels = getModelMapper().listUsersByRoleId(pageRequest.getRoleId());
        PageInfo pageInfo = new PageInfo(userModels);
        List<UserDto> userDtos = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(userModels)) {
            userModels.forEach(u -> userDtos.add(userService.userModel2UserDto(u)));
        }
        pageInfo.setList(userDtos);
        return pageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindUserRoles(String userId, List<String> roleIds, boolean cleanOldRoles) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);

        var objResult = userService.get(userId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_EXISTED);
        }

        if (cleanOldRoles) {
            // 清除用户角色
            boolean success = deleteByUserId(userId);
            if (!success) {
                throw new MpRuntimeException(AuthErrorCode.USER_ROLE_DELETE_ERROR);
            }
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
    @Transactional(rollbackFor = Exception.class)
    public boolean bindRoleUsers(String roleId, List<String> userIds) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);

        var objResult = roleService.get(roleId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            throw new MpRuntimeException(AuthErrorCode.ROLE_NOT_EXISTED);
        }

        if (CollectionUtil.isEmpty(userIds)) {
            // 完成，直接返回
            return true;
        }

        // 添加用户角色
        userIds.forEach(uid -> {
            // todo 校验角色是否存在
            UserRoleModel userRole = new UserRoleModel();
            userRole.setUser(uid);
            userRole.setRole(roleId);
            userRole.setTenant(AuthConstant.DEFAULT_TENANT);
            create(userRole);
        });

        return true;
    }

    @Override
    public boolean deleteByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);

//        var objResult = userService.get(userId).getSource();
//        if (objResult == null || objResult.getObj() == null) {
//            new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");
//        }

        getModelMapper().deleteByUserId(userId);

        return true;
    }

    @Override
    public boolean deleteByRoleId(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);

//        var objResult = roleService.get(roleId).getSource();
//        if (objResult == null || objResult.getObj() == null) {
//            new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");
//        }

        getModelMapper().deleteByRoleId(roleId);

        return true;
    }

    @Override
    public List<UserRoleStatistic> getUserRoleStatistics() {
        List<UserRoleStatistic> statistics = getModelMapper().getUserRoleStatistics();
        return statistics;
    }

    @Override
    public boolean deleteByRoleIdAndUserIds(String roleId, List<String> userIds) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);
        opUserPermService.checkByTargetUserIds(userIds);

        var objResult = roleService.get(roleId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            throw new MpRuntimeException(AuthErrorCode.ROLE_NOT_EXISTED);
        }

        if (CollectionUtil.isEmpty(userIds)) {
            // 完成，直接返回
            return true;
        }

        // 删除用户角色
        getModelMapper().deleteByRoleIdAndUserIds(roleId, userIds);
        return true;
    }


}
