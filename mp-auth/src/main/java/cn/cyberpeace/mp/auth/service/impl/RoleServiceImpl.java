package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.role.RoleBo;
import cn.cyberpeace.mp.auth.bo.role.RolePermBo;
import cn.cyberpeace.mp.auth.bo.target.PermissionBo;
import cn.cyberpeace.mp.auth.bo.target.TargetRoleBo;
import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.request.role.RolePageRequest;
import cn.cyberpeace.mp.auth.request.role.RoleRequest;
import cn.cyberpeace.mp.auth.service.AdminConfigService;
import cn.cyberpeace.mp.auth.service.OperateRolePermissionService;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserCacheService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.fields.RoleFields;
import cn.cyberpeace.mp.auth.service.sender.AuthStatusRabbitmqSender;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.SQLUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.IdUtils;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;


@Service
@Slf4j
@CacheConfig(cacheManager = AuthCacheConfig.CACHE_MANAGER_NORMAL)
public class RoleServiceImpl extends BaseModelServiceImpl<RoleModel, RoleMapper, RoleFields, QueryParams>
        implements RoleService {

    public static final String CACHE_VAL_ROLE_GETBY_NAME = "ROLES_GET_BY_NAME";
    public static final String CACHE_VAL_ROLE_LISTBY_NAMES = "ROLES_LISTBY_NAMES";


    @Autowired
    private RolePermissionModelService rolePermissionService;

    @Autowired
    @Qualifier("targetServiceImpl")

    private TargetService targetService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private OperateRolePermissionService opRolePermCheckService;

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private AuthStatusRabbitmqSender messageSender;

    @Autowired
    private AdminConfigService adminConfigService;

    @Autowired
    public RoleServiceImpl(RoleMapper modelMapper, RoleFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean> delete(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);
        deleteReleated(roleId);
        var ret = super.delete(roleId);
        if (Objects.nonNull(ret.getSource()) && ret.getSource()) {
            userCacheService.evictAllUserCache();
            userCacheService.evictAllTargetsCache();
            messageSender.send(Collections.singletonList(roleId), AuthConstant.MSG_TYPE_ROLE_DELETE, AuthConstant.ROUTING_KEY_ROLE_DELETE, null);
        }
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        for (String id : ids) {
            deleteReleated(id);
        }

        var ret = super.bulkDelete(ids);
        if (Objects.nonNull(ret.getSource()) && ret.getSource() > 0) {
            userCacheService.evictAllUserCache();
            userCacheService.evictAllTargetsCache();
            messageSender.send(Collections.singletonList(ids), AuthConstant.MSG_TYPE_ROLE_DELETE, AuthConstant.ROUTING_KEY_ROLE_DELETE, null);
        }
        return ret;
    }

    @Override
    public ObjServiceResult<RoleModel> update(RoleModel obj, String[] fields) {
        if (StringUtils.isBlank(obj.getIdentifier())) {
            obj.setIdentifier(null);
        }
        return super.update(obj, fields);
    }

    @Override
    public List<RoleModel> listByTenantId(String tenantId) {
        ParamCheck.notBlank(tenantId, AuthBasei18nCode.TENANT_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RoleModel::getVisible,true)
                .filterEqual(RoleModel::getTenant, tenantId);
        List<RoleModel> roles = getModelMapper().list(humanCondition);
        return roles;
    }


    @Override
    public List<RoleModel> listAll(RolePageRequest rolePageRequest) {
        List<String> excludeRoleIds = opRolePermCheckService.getExcludeRoleIdsByLoginUserForSearchingRoles();
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(RoleModel::getVisible, true);
        if (CollUtil.isNotEmpty(excludeRoleIds)) {
            humanCondition.filterNotIn(RoleModel::getId, excludeRoleIds.toArray());
        }

        if (rolePageRequest.getOrders() != null && rolePageRequest.getOrders().length > 0) {
            var orders = CollUtil.newArrayList(rolePageRequest.getOrders());
            if (CollUtil.isNotEmpty(orders)) {
                for (String order : orders) {
                    humanCondition.orderBy(order);
                }
            }
        }
        List<RoleModel> roles = getModelMapper().list(humanCondition);
        return roles;
    }

    @Override
    public List<RoleModel> listByOrgId(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RoleModel::getVisible, true)
                .filterEqual(RoleModel::getOrg, orgId);
        List<RoleModel> roles = getModelMapper().list(humanCondition);
        return roles;
    }

    @Override
    public RoleModel getByIdent(String ident) {
        ParamCheck.notBlank(ident, AuthBasei18nCode.ROLE_IDENTIFIER);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RoleModel::getVisible, true)
                .filterEqual(RoleModel::getIdentifier, ident);
        RoleModel model = getModelMapper().first(humanCondition);
        return model;
    }

    @Override
    @Cacheable(value = CACHE_VAL_ROLE_GETBY_NAME)
    public RoleModel getByName(String name) {
        ParamCheck.notBlank(name, AuthBasei18nCode.ROLE_NAME);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RoleModel::getVisible, true)
                .filterEqual(RoleModel::getName, name);
        RoleModel model = getModelMapper().first(humanCondition);
        return model;
    }


    @Override
    @Cacheable(value = CACHE_VAL_ROLE_LISTBY_NAMES)
    public List<RoleModel> listByNames(List<String> names) {
        ParamCheck.notEmpty(names, AuthBasei18nCode.ROLE_NAME);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RoleModel::getVisible, true)
                .filterIn(RoleModel::getName, names.toArray());
        List<RoleModel> roleModels = getModelMapper().list(humanCondition);
        return roleModels;
    }

    @Override
    public boolean exists(String id) {
        ParamCheck.notBlank(id, AuthBasei18nCode.ROLE_ID);

        RoleModel roleModel = getModelMapper().get(id);
        return Objects.nonNull(roleModel);
    }

    @Override
    public ListObjServiceResult getPageWithUserCnt(RolePageRequest rolePageRequest) {
        ArrayList<String> orders = null;
        if (rolePageRequest.getOrders() != null && rolePageRequest.getOrders().length > 0) {
            orders = CollUtil.newArrayList(rolePageRequest.getOrders());
        }
        String likeRoleName = SQLUtils.escapeLike(rolePageRequest.getName());
        ListObjServiceResult result = listWithUserCnt(likeRoleName, orders, opRolePermCheckService.getExcludeRoleIdsByLoginUserForSearchingRoles(), rolePageRequest.getPage(), rolePageRequest.getPageSize());

        return result;
    }

    @Override
    public RoleBo getRoleDto(String id) {
        ParamCheck.notBlank(id, AuthBasei18nCode.ROLE_ID);
        List<String> excludeRoleIds = opRolePermCheckService.getExcludeRoleIdsByLoginUserForSearchingRoles();
        if (excludeRoleIds.contains(id.trim())) {
            return null;
        }
        RoleModel roleModel = getModelMapper().get(id);
        RoleBo roleDto = roleModel2RoleDto(roleModel);
        return roleDto;
    }

    @Override
    public RolePermBo getRolePermDto(String id) {
        ParamCheck.notBlank(id, AuthBasei18nCode.ROLE_ID);
        List<String> excludeRoleIds = opRolePermCheckService.getExcludeRoleIdsByLoginUserForSearchingRoles();
        if (excludeRoleIds.contains(id.trim())) {
            return null;
        }
        RoleModel roleModel = getModelMapper().get(id);
        RolePermBo rolePermBo = roleModel2RolePermDto(roleModel);
        return rolePermBo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleModel add(RoleRequest createRequest) {
        ParamCheck.notNull(createRequest, AuthBasei18nCode.REQUEST_PARAMS);

        RoleModel roleModel = new RoleModel();
        BeanUtils.copyProperties(createRequest, roleModel);

        createCheck(roleModel);

        if (StringUtils.isBlank(roleModel.getIdentifier())) {
            
            roleModel.setIdentifier(IdUtils.globalId());
        }

        ObjServiceResult<RoleModel> result = create(roleModel);
        ResultUtils.throwExceptionIfError(result, AuthErrorCode.OP_ERROR);

        RoleModel roleCreated = result.getSource().getObj();
        String roleId = roleCreated.getId();
        log.info("role created id:{}", roleId);

        
        List<String> targetIds = createRequest.getTargetIds();
        if (CollUtil.isNotEmpty(targetIds)) {
            
            TreeSet<String> set = new TreeSet<>(targetIds);

            set.forEach(id -> {
                boolean isTargetExist = targetService.exists(id);
                if (!isTargetExist) {
                    log.error("target id:{} not existed!", id);
                    throw new MpRuntimeException(AuthErrorCode.TARGET_NOT_EXISTED.format(id));
                }
                RolePermissionModel rolePermissionModel = new RolePermissionModel();
                rolePermissionModel.setOwner(roleId);
                rolePermissionModel.setTarget(id);
                
                ObjServiceResult re = rolePermissionService.create(rolePermissionModel);
                ResultUtils.throwExceptionIfError(re, AuthErrorCode.ROLE_TARGET_ADD_ERROR);
            });
        }

        return roleCreated;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleModel modify(RoleRequest modifyRequest) {
        ParamCheck.notNull(modifyRequest, AuthBasei18nCode.REQUEST_PARAMS);
        String roleId = modifyRequest.getId();
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);
        if (Objects.equals(AuthConstant.ROLE_ADMIN_ID, roleId)) {
            log.error("ADMIN ROLE can not update！");
            throw new MpRuntimeException("ADMIN ROLE can not update!");
        }

        RoleModel roleUpdate = new RoleModel();
        BeanUtils.copyProperties(modifyRequest, roleUpdate);
        ObjServiceResult<RoleModel> updateResult = update(roleUpdate);
        ResultUtils.throwExceptionIfError(updateResult, AuthErrorCode.OP_ERROR);


        
        boolean cleanSuccess = rolePermissionService.deleteByRoleId(roleId);
        if (!cleanSuccess) {
            throw new MpRuntimeException(AuthErrorCode.ROLE_TARGET_DELETE_ERROR);
        }

        
        if (CollUtil.isNotEmpty(modifyRequest.getTargetIds())) {
            modifyRequest.getTargetIds().forEach(id -> {
                boolean isTargetExist = targetService.exists(id);
                if (!isTargetExist) {
                    log.error("target id:{} not existed!", id);
                    throw new MpRuntimeException(AuthErrorCode.TARGET_NOT_EXISTED.format(id));
                }
                RolePermissionModel rolePermissionModel = new RolePermissionModel();
                rolePermissionModel.setOwner(roleId);
                rolePermissionModel.setTarget(id);
                
                ObjServiceResult re = rolePermissionService.create(rolePermissionModel);
                ResultUtils.throwExceptionIfError(re, AuthErrorCode.ROLE_TARGET_ADD_ERROR);
            });
        }

        userCacheService.evictAllUserCache();
        userCacheService.evictAllTargetsCache();
        messageSender.send(Collections.singletonList(roleId), AuthConstant.MSG_TYPE_ROLE_MODIFY, AuthConstant.ROUTING_KEY_ROLE_MODIFY, null);
        return updateResult.getSource().getObj();
    }

    @Override
    public List<String> getSystemUnVisibleRoleIds() {
        var models = getModelMapper().list(new HumanCondition().filterEqual(RoleModel::getVisible, false));
        if (CollectionUtils.isEmpty(models)) {
            return new ArrayList<>();
        }
        return models.stream().map(m -> m.getId()).collect(Collectors.toList());
    }


    private RoleBo roleModel2RoleDto(RoleModel roleModel) {
        if (Objects.isNull(roleModel)) {
            return null;
        }

        RoleBo roleBo = new RoleBo();
        cn.cyberpeace.mp.base.util.basic.BeanUtils.copyProperties(roleModel, roleBo);

        List<TargetModel> targets = getRoleTargets(roleModel);

        roleBo.setTargets(targets);
        return roleBo;
    }


    private RolePermBo roleModel2RolePermDto(RoleModel roleModel) {
        if (Objects.isNull(roleModel)) {
            return null;
        }

        RolePermBo rolePermBo = new RolePermBo();
        cn.cyberpeace.mp.base.util.basic.BeanUtils.copyProperties(roleModel, rolePermBo);

        List<TargetModel> targets = getRoleTargets(roleModel);

        rolePermBo.setTargets(targetModel2PermissionDto(targets));
        return rolePermBo;
    }

    private List<TargetModel> getRoleTargets(RoleModel roleModel) {
        List<TargetModel> targets = new ArrayList<>();
        if (Objects.equals(roleModel.getId(), AuthConstant.ROLE_ADMIN_ID)) {
            
            targets = targetService.listAll(AuthConstant.DEFAULT_TENANT, null);
        } else {
            targets = rolePermissionService.listTargetsByRoleIds(Arrays.asList(roleModel.getId()));
        }
        return targets;
    }

    private List<PermissionBo> targetModel2PermissionDto(List<TargetModel> targets) {
        List<PermissionBo> permissionBos =
                targets.stream().map(t -> {
                    PermissionBo permissionBo = new PermissionBo();
                    BeanUtils.copyProperties(t, permissionBo);
                    permissionBo.setPerm("R/W/X");
                    permissionBo.setIsEnabled(1);
                    return permissionBo;
                }).collect(Collectors.toList());
        return permissionBos;
    }


    private void deleteReleated(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return;
        }

        
        if (Objects.equals(AuthConstant.ROLE_ADMIN_ID, roleId)) {
            log.info("ADMIN ROLE can not delete！");
            throw new MpRuntimeException(AuthErrorCode.ROLE_CANNOT_DELETE_ADMIN);
        }

        
        if (Objects.equals(roleId, adminConfigService.getRegisterDefaultRoleId())) {
            log.info("Register default role can not delete！");
            throw new MpRuntimeException(AuthErrorCode.ROLE_CANNOT_DELETE_FOR_REGISTER_DEFAULT);
        }

        List<UserModel> users = userRoleService.listUsersByRoleIdNoRestrict(roleId);
        if (CollUtil.isNotEmpty(users)) {
            throw new MpRuntimeException(AuthErrorCode.ROLE_CANNOT_DELETE_WITH_USER_EXISTED);
        }

        boolean deleteRolePermissionSuccess = rolePermissionService.deleteByRoleId(roleId);
        boolean deleteUserRoleSuccess = userRoleService.deleteByRoleId(roleId);

        if (!deleteRolePermissionSuccess || !deleteUserRoleSuccess) {
            log.info("deleteRolePermissionSuccess:{}, deleteUserRoleSuccess:{}", deleteRolePermissionSuccess, deleteUserRoleSuccess);
            throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
        }
    }

    private void createCheck(RoleModel roleModel) {
        if (StringUtils.isNotBlank(roleModel.getName())) {
            if (getByName(roleModel.getName()) != null) {
                log.error("role name {} already existed!", roleModel.getName());
                throw new MpRuntimeException(AuthErrorCode.ROLE_NAME_EXISTED);
            }
        }

        if (Objects.equals(AuthConstant.ROLE_ADMIN, roleModel.getIdentifier())) {
            throw new MpRuntimeException(AuthErrorCode.ROLE_IDENT_CANNOT_BE_ADMIN);
        }
    }

    public ListObjServiceResult listWithUserCnt(String roleName, List<String> orders, List<String> excludeRoleIds, int page, int pageSize) {
        HumanCondition roleCondition = new HumanCondition();
        roleCondition.filterEqual(RoleModel::getVisible, true);
        if (CollUtil.isNotEmpty(excludeRoleIds)) {
            roleCondition.filterNotIn(RoleModel::getId, excludeRoleIds.toArray());
        }
        if (StringUtils.isNotBlank(roleName)) {
            roleCondition.filterContains(RoleModel::getName, roleName);
        }
        if (CollUtil.isNotEmpty(orders)) {
            for (String order : orders) {
                roleCondition.orderBy(order);
            }
        }
        int total = getModelMapper().total(roleCondition);
        roleCondition.pagination(page, pageSize);
        List<RoleModel> roleModels = getModelMapper().list(roleCondition);
        List<String> roleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());

        if (CollUtil.isEmpty(roleIds)) {
            roleIds.add("");
        }
        HumanCondition userRoleCondition = new HumanCondition()
                .filterIn(UserRoleModel::getRole, roleIds.toArray())
                .select(UserRoleModel::getRole)
                .selectAs(Count.f(UserRoleModel::getUser).distinct(), "userCnt")
                .groupBy(UserRoleModel::getRole);

        List<Map<String, Object>> maps = userRoleMapper.originList(userRoleCondition);
        Map<String, Integer> roleCountMap = new HashMap<>(maps.size());
        maps.stream().forEach(map -> {
            roleCountMap.put((String) map.get("role"), ((Long) map.get("userCnt")).intValue());
        });

        List<RoleBo> rolebos = new ArrayList<>();
        for (RoleModel roleModel : roleModels) {
            RoleBo roleBo = new RoleBo();
            BeanUtils.copyProperties(roleModel, roleBo);
            roleBo.setUserCnt(roleCountMap.getOrDefault(roleModel.getId(), 0));
            rolebos.add(roleBo);
        }

        List<TargetModel> allTargets = targetService.listAll(AuthConstant.DEFAULT_TENANT, null);

        rolebos.forEach(r -> {
            if (r.getId().equals(AuthConstant.ROLE_ADMIN_ID)) {
                r.setTargets(allTargets);
            }
        });

        List<String> roleBoIds =
                rolebos.stream().map(r -> {
                    if (CollUtil.isEmpty(r.getTargets())) {
                        return r.getId();
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(roleBoIds)) {
            List<TargetRoleBo> targetRoleBos = rolePermissionService.listTargetRoleDtosByRoleIds(roleBoIds);
            if (CollUtil.isNotEmpty(targetRoleBos)) {
                rolebos.forEach(r -> {
                    List<TargetModel> targetModels = targetRoleBos.stream().map(tr -> {
                        if (Objects.equals(tr.getRoleId(), r.getId())) {
                            TargetModel targetModel = new TargetModel();
                            BeanUtils.copyProperties(tr, targetModel);
                            return targetModel;
                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
                    r.setTargets(targetModels);
                });
            }
        }
        ListObjServiceResult<RoleBo> result = new ListObjServiceResult(new ListObjResult(rolebos, total).asResult()).asResult();
        return result;
    }

}
