package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.role.UserRoleStatisticBo;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.bo.user.UserOrgRoleBo;
import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.repository.TenantUserMapper;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.request.role.UserRolePageRequest;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.UserCacheService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserRoleFields;
import cn.cyberpeace.mp.auth.service.sender.AuthStatusRabbitmqSender;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@CacheConfig(cacheManager = AuthCacheConfig.CACHE_MANAGER_MEDIUM)
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
    private UserCacheService userCacheService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TenantUserMapper tenantUserMapper;

    @Autowired
    private AuthStatusRabbitmqSender messageSender;

    @Autowired
    public UserRoleServiceImpl(UserRoleMapper modelMapper, UserRoleFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Cacheable(value = CACHE_VAL_ROLES)
    public List<RoleModel> listByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        List<RoleModel> roles = getRolesByUserId(userId);
        return roles;
    }

    @Override
    @Cacheable(value = CACHE_VAL_ROLES)
    public List<RoleModel> listByUserIds(List<String> userIds) {
        Assert.notEmpty(userIds, "userIds不能为空！");
        HumanCondition userRoleCondition = new HumanCondition()
                .filterIn(UserRoleModel::getUser, userIds.toArray())
                .select(UserRoleModel::getRole);
        List<UserRoleModel> userRoleModels = getModelMapper().list(userRoleCondition);
        List<String> roleIds = new ArrayList<>();
        userRoleModels.forEach(userRoleModel -> roleIds.add(userRoleModel.getRole()));
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        HumanCondition roleCondition = new HumanCondition()
                .filterIn(RoleModel::getId, roleIds.toArray())
                .distinct();
        List<RoleModel> roles = roleMapper.list(roleCondition);
        return roles;
    }

    @Override
    public List<UserModel> listUsersByRoleIdNoRestrict(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);
        List<UserOrgRoleBo> userOrgRoleBos = listUsersByRoleId(roleId);
        if (CollectionUtil.isEmpty(userOrgRoleBos)) {
            return Collections.emptyList();
        }

        List<UserModel> userModels =
            userOrgRoleBos.stream().map(uor -> {
                UserModel userModel = new UserModel();
                BeanUtils.copyProperties(uor, userModel);
                userService.setDefaultIfNoAvatar(userModel);
                return userModel;
            }).collect(Collectors.toList());

        return userModels;
    }

    @Override
    public ListObjServiceResult pageByUserId(UserRolePageRequest pageRequest) {
        pageRequest.ensure();
        ParamCheck.notBlank(pageRequest.getUserId(), AuthBasei18nCode.USER_ID);
        opUserPermService.checkByTargetUserIdsForSearching(Arrays.asList(pageRequest.getUserId()));
        
        ListObjServiceResult result = getPageRolesByUserId(pageRequest.getUserId(), pageRequest.getPage(), pageRequest.getPageSize());

        
        return result;
    }

    @Override
    public ListObjServiceResult pageByRoleId(UserRolePageRequest pageRequest) {
        pageRequest.ensure();
        ParamCheck.notBlank(pageRequest.getRoleId(), AuthBasei18nCode.ROLE_ID);
        List<String> excludeRoleIds = opUserPermService.getExcludeRoleIdsByLoginUserForSearchingUsers();
        if (CollectionUtil.isNotEmpty(excludeRoleIds) && excludeRoleIds.contains(pageRequest.getRoleId())) {
            new PageInfo<>(Collections.emptyList());
        }
        ListObjServiceResult result = listPageUsersByRoleId(pageRequest.getRoleId(), pageRequest.getPage(), pageRequest.getPageSize());
        return result;
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
            
            boolean success = deleteByUserId(userId);
            if (!success) {
                throw new MpRuntimeException(AuthErrorCode.USER_ROLE_DELETE_ERROR);
            }
        }

        if (CollectionUtil.isEmpty(roleIds)) {
            
            return true;
        }

        
        roleIds.forEach(rid -> {
            
            UserRoleModel userRole = new UserRoleModel();
            userRole.setUser(userId);
            userRole.setRole(rid);
            userRole.setTenant(AuthConstant.DEFAULT_TENANT);
            create(userRole);
        });

        userCacheService.evictAllUserCache();
        messageSender.send(roleIds, AuthConstant.MSG_TYPE_ROLE_MODIFY, AuthConstant.ROUTING_KEY_ROLE_MODIFY, null);
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
            
            return true;
        }

        
        userIds.forEach(uid -> {
            
            UserRoleModel userRole = new UserRoleModel();
            userRole.setUser(uid);
            userRole.setRole(roleId);
            userRole.setTenant(AuthConstant.DEFAULT_TENANT);
            create(userRole);
        });

        userCacheService.evictAllUserCache();
        messageSender.send(Collections.singletonList(roleId), AuthConstant.MSG_TYPE_ROLE_MODIFY, AuthConstant.ROUTING_KEY_ROLE_MODIFY, null);
        return true;
    }

    @Override
    public boolean deleteByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);





        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(UserRoleModel::getUser, userId);
        getModelMapper().filterDelete(humanCondition);
        userCacheService.evictAllUserCache();
        return true;
    }

    @Override
    public boolean deleteByRoleId(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(UserRoleModel::getRole, roleId);
        getModelMapper().filterDelete(humanCondition);
        userCacheService.evictAllUserCache();
        messageSender.send(Collections.singletonList(roleId), AuthConstant.MSG_TYPE_ROLE_MODIFY, AuthConstant.ROUTING_KEY_ROLE_MODIFY, null);
        return true;
    }

    @Override
    public List<UserRoleStatisticBo> getUserRoleStatistics() {
        HumanCondition userRoleCondition = new HumanCondition()
                .select(UserRoleModel::getRole)
                .selectAs(Count.f(UserRoleModel::getUser), "cnt")
                .groupBy(UserRoleModel::getRole)
                .orderBy("-cnt");
        List<Map<String, Object>> roleCountMaps = getModelMapper().originList(userRoleCondition);

        HumanCondition roleCondition = new HumanCondition()
                .filterEqual(RoleModel::getVisible,true)
                .select(RoleModel::getId, RoleModel::getName)
                .distinct();
        List<RoleModel> roleModels = roleMapper.list(roleCondition);
        Map<String, String> roleMap = new HashMap<>(roleModels.size());
        roleModels.stream().forEach(roleModel -> roleMap.put(roleModel.getId(), roleModel.getName()));

        List<UserRoleStatisticBo> statistics = new ArrayList<>();
        for (Map<String, Object> roleCountMap : roleCountMaps) {
            String roleId = (String) roleCountMap.get("role");
            Integer cnt = ((Long) roleCountMap.get("cnt")).intValue();
            String roleName = roleMap.get(roleId);
            if (roleName != null) {
                UserRoleStatisticBo userRoleStatisticBo = new UserRoleStatisticBo();
                userRoleStatisticBo.setRoleId(roleId);
                userRoleStatisticBo.setUserCnt(cnt);
                userRoleStatisticBo.setRoleName(roleName);
                statistics.add(userRoleStatisticBo);
            }
        }
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
            
            return true;
        }

        
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(UserRoleModel::getRole, roleId);
        if (CollectionUtil.isNotEmpty(userIds)) {
            humanCondition.filterIn(UserRoleModel::getUser, userIds.toArray());
        }
        getModelMapper().filterDelete(humanCondition);

        userCacheService.evictAllUserCache();
        messageSender.send(Collections.singletonList(roleId), AuthConstant.MSG_TYPE_ROLE_MODIFY, AuthConstant.ROUTING_KEY_ROLE_MODIFY, null);
        return true;
    }

    private List<RoleModel> getRolesByUserId(String userId) {
        HumanCondition userRoleCondition = new HumanCondition()
                .filterEqual(UserRoleModel::getUser, userId)
                .select(UserRoleModel::getRole);
        List<UserRoleModel> userRoleModels = getModelMapper().list(userRoleCondition);
        List<String> roles = userRoleModels.stream()
                .map(UserRoleModel::getRole)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(roles)) {
            return new ArrayList<>();
        }
        HumanCondition roleCondition = new HumanCondition()
                .filterIn(RoleModel::getId, roles.toArray())
                .distinct();
        return roleMapper.list(roleCondition);
    }

    private ListObjServiceResult getPageRolesByUserId(String userId, int page, int pageSize) {
        HumanCondition userRoleCondition = new HumanCondition()
                .filterEqual(UserRoleModel::getUser, userId)
                .select(UserRoleModel::getRole);
        List<UserRoleModel> userRoleModels = getModelMapper().list(userRoleCondition);
        List<String> roles = userRoleModels.stream()
                .map(UserRoleModel::getRole)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(roles)) {
            return new ListObjServiceResult(new ListObjResult(new ArrayList(), 0).asResult()).asResult();
        }
        HumanCondition roleCondition = new HumanCondition()
                .filterIn(RoleModel::getId, roles.toArray())
                .distinct();
        int total = roleMapper.total(roleCondition);
        roleCondition.pagination(page, pageSize);
        List<RoleModel> models = roleMapper.list(roleCondition);
        ListObjServiceResult<RoleModel> result = new ListObjServiceResult(new ListObjResult(models, total).asResult()).asResult();
        return result;
    }

    public List<UserOrgRoleBo> listUsersByRoleId(String roleId) {
        HumanCondition roleIdCondition = new HumanCondition()
                .filterEqual(UserRoleModel::getRole, roleId)
                .select(UserRoleModel::getUser)
                .distinct();
        List<UserRoleModel> userRoleModels = getModelMapper().list(roleIdCondition);
        List<String> uIds = userRoleModels.stream()
                .map(UserRoleModel::getUser)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(uIds)) {
            return new ArrayList<>();
        }

        HumanCondition userCondition = new HumanCondition()
                .filterIn(UserModel::getId, uIds.toArray())
                .orderBy(UserModel::getUpdateTime, true)
                .orderBy(UserModel::getUsername)
                .distinct();
        List<UserModel> userModels = userMapper.list(userCondition);

        HumanCondition orgCondition = new HumanCondition()
                .filterIn(TenantUserModel::getUser, uIds.toArray())
                .select(TenantUserModel::getUser)
                .selectAs(GroupConcat.f(TenantUserModel::getOrg).distinct(), "orgs")
                .groupBy(TenantUserModel::getUser);
        List<Map<String, Object>> userOrgMaps = tenantUserMapper.originList(orgCondition);
        Map<String, String> userOrgMap = new HashMap<>(userOrgMaps.size());
        userOrgMaps.stream().forEach(map -> {
            userOrgMap.put((String) map.get("user"), (String) map.getOrDefault("orgs", ""));
        });

        HumanCondition roleCondition = new HumanCondition()
                .filterIn(UserRoleModel::getUser, uIds.toArray())
                .select(UserRoleModel::getUser)
                .selectAs(GroupConcat.f(UserRoleModel::getRole).distinct(), "roles")
                .groupBy(UserRoleModel::getUser);
        List<Map<String, Object>> userRoleMaps = getModelMapper().originList(roleCondition);
        Map<String, String> userRoleMap = new HashMap<>(userRoleMaps.size());
        userRoleMaps.stream().forEach(map -> {
            userRoleMap.put((String) map.get("user"), (String) map.getOrDefault("roles", ""));
        });

        List<UserOrgRoleBo> UserOrgRoleBos = new ArrayList<>(userModels.size());
        for (UserModel userModel : userModels) {
            UserOrgRoleBo userOrgRoleBo = new UserOrgRoleBo();
            BeanUtils.copyProperties(userModel, userOrgRoleBo);
            userOrgRoleBo.setRoles(userRoleMap.get(userModel.getId()));
            userOrgRoleBo.setOrgs(userOrgMap.get(userModel.getId()));
            UserOrgRoleBos.add(userOrgRoleBo);
        }
        return UserOrgRoleBos;
    }

    public ListObjServiceResult listPageUsersByRoleId(String roleId, int page, int pageSize) {
        HumanCondition roleIdCondition = new HumanCondition()
                .filterEqual(UserRoleModel::getRole, roleId)
                .select(UserRoleModel::getUser)
                .distinct();
        List<UserRoleModel> userRoleModels = getModelMapper().list(roleIdCondition);
        List<String> uIds = userRoleModels.stream()
                .map(UserRoleModel::getUser)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(uIds)) {
            return new ListObjServiceResult(new ListObjResult(new ArrayList(), 0).asResult()).asResult();
        }

        HumanCondition userCondition = new HumanCondition()
                .filterIn(UserModel::getId, uIds.toArray())
                .orderBy(UserModel::getUpdateTime, true)
                .orderBy(UserModel::getUsername)
                .distinct();
        int total = userMapper.total(userCondition);
        userCondition.pagination(page, pageSize);
        List<UserModel> userModels = userMapper.list(userCondition);

        HumanCondition orgCondition = new HumanCondition()
                .filterIn(TenantUserModel::getUser, uIds.toArray())
                .select(TenantUserModel::getUser)
                .selectAs(GroupConcat.f(TenantUserModel::getOrg).distinct(), "orgs")
                .groupBy(TenantUserModel::getUser);
        List<Map<String, Object>> userOrgMaps = tenantUserMapper.originList(orgCondition);
        Map<String, String> userOrgMap = new HashMap<>(userOrgMaps.size());
        userOrgMaps.stream().forEach(map -> {
            userOrgMap.put((String) map.get("user"), (String) map.getOrDefault("orgs", ""));
        });

        HumanCondition roleCondition = new HumanCondition()
                .filterIn(UserRoleModel::getUser, uIds.toArray())
                .select(UserRoleModel::getUser)
                .selectAs(GroupConcat.f(UserRoleModel::getRole).distinct(), "roles")
                .groupBy(UserRoleModel::getUser);
        List<Map<String, Object>> userRoleMaps = getModelMapper().originList(roleCondition);
        Map<String, String> userRoleMap = new HashMap<>(userRoleMaps.size());
        userRoleMaps.stream().forEach(map -> {
            userRoleMap.put((String) map.get("user"), (String) map.getOrDefault("roles", ""));
        });

        List<UserOrgRoleBo> userOrgRoleBos = new ArrayList<>(userModels.size());
        for (UserModel userModel : userModels) {
            UserOrgRoleBo userOrgRoleBo = new UserOrgRoleBo();
            BeanUtils.copyProperties(userModel, userOrgRoleBo);
            userOrgRoleBo.setRoles(userRoleMap.get(userModel.getId()));
            userOrgRoleBo.setOrgs(userOrgMap.get(userModel.getId()));
            userOrgRoleBos.add(userOrgRoleBo);
        }
        List<UserBo> userBos = userService.userOrgRoleDtos2UserDtos(userOrgRoleBos);
        ListObjServiceResult<UserOrgRoleBo> result = new ListObjServiceResult(new ListObjResult(userBos, total).asResult()).asResult();
        return result;
    }

}
