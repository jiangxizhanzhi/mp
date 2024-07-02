package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.role.RoleBo;
import cn.cyberpeace.mp.auth.bo.role.RolePermBo;
import cn.cyberpeace.mp.auth.bo.target.PermissionBo;
import cn.cyberpeace.mp.auth.bo.target.TargetRoleBo;
import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.*;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.request.role.RolePageRequest;
import cn.cyberpeace.mp.auth.request.role.RoleRequest;
import cn.cyberpeace.mp.auth.service.*;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ClassName RoleServiceImpl
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 * @see RoleService
 */
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
//    @Qualifier("templateTargetServiceImpl")
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

    /**
     * RoleServiceImpl
     *
     * @param modelMapper
     * @param modelFields
     */
    @Autowired
    public RoleServiceImpl(RoleMapper modelMapper, RoleFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    /**
     * delete
     *
     * @param roleId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean> delete(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);
        deleteReleated(roleId);
        ServiceResult<Boolean> ret = super.delete(roleId);
        if (Objects.nonNull(ret.getSource()) && ret.getSource()) {
            userCacheService.evictAllUserCache();
            userCacheService.evictAllTargetsCache();
            messageSender.send(Collections.singletonList(roleId), AuthConstant.MSG_TYPE_ROLE_DELETE, AuthConstant.ROUTING_KEY_ROLE_DELETE, null);
        }
        return ret;
    }

    /**
     * bulkDelete
     *
     * @param ids
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        for (String id : ids) {
            deleteReleated(id);
        }

        ServiceResult<Integer> ret = super.bulkDelete(ids);
        if (Objects.nonNull(ret.getSource()) && ret.getSource() > 0) {
            userCacheService.evictAllUserCache();
            userCacheService.evictAllTargetsCache();
            messageSender.send(Collections.singletonList(ids), AuthConstant.MSG_TYPE_ROLE_DELETE, AuthConstant.ROUTING_KEY_ROLE_DELETE, null);
        }
        return ret;
    }

    /**
     * update
     *
     * @param obj
     * @param fields
     * @return
     */
    @Override
    public ObjServiceResult<RoleModel> update(RoleModel obj, String[] fields) {
        if (StringUtils.isBlank(obj.getIdentifier())) {
            obj.setIdentifier(null);
        }
        return super.update(obj, fields);
    }

    /**
     * listByTenantId
     *
     * @param tenantId
     * @return
     */
    @Override
    public List<RoleModel> listByTenantId(String tenantId) {
        ParamCheck.notBlank(tenantId, AuthBasei18nCode.TENANT_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RoleModel::getVisible,true)
                .filterEqual(RoleModel::getTenant, tenantId);
        List<RoleModel> roles = getModelMapper().list(humanCondition);
        return roles;
    }

    /**
     * listByIds
     *
     * @param ids
     * @return
     */
    @Override
    public List<RoleModel> listByIds(Collection<String> ids) {
        HumanCondition humanCondition = new HumanCondition().filterIn("id", ids.toArray());
        return getModelMapper().list(humanCondition);
    }


    /**
     * listAll
     *
     * @param rolePageRequest
     * @return
     */
    @Override
    public List<RoleModel> listAll(RolePageRequest rolePageRequest) {
        List<String> excludeRoleIds = opRolePermCheckService.getExcludeRoleIdsByLoginUserForSearchingRoles();
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(RoleModel::getVisible, true);
        if (CollectionUtils.isNotEmpty(excludeRoleIds)) {
            humanCondition.filterNotIn(RoleModel::getId, excludeRoleIds.toArray());
        }

        if (rolePageRequest.getOrders() != null && rolePageRequest.getOrders().length > 0) {
            List<String> orders = Arrays.asList(rolePageRequest.getOrders());
            if (CollectionUtils.isNotEmpty(orders)) {
                for (String order : orders) {
                    humanCondition.orderBy(order);
                }
            }
        }
        List<RoleModel> roles = getModelMapper().list(humanCondition);
        return roles;
    }

    /**
     * getByName
     *
     * @param name
     * @return
     */
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


    /**
     * listByNames
     *
     * @param names
     * @return
     */
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

    /**
     * exists
     *
     * @param id
     * @return boolean
     */
    @Override
    public boolean exists(String id) {
        ParamCheck.notBlank(id, AuthBasei18nCode.ROLE_ID);

        RoleModel roleModel = getModelMapper().get(id);
        return Objects.nonNull(roleModel);
    }

    /**
     * getPageWithUserCnt
     *
     * @param rolePageRequest
     * @return
     */
    @Override
    public ListObjServiceResult getPageWithUserCnt(RolePageRequest rolePageRequest) {
        ArrayList<String> orders = null;
        if (rolePageRequest.getOrders() != null && rolePageRequest.getOrders().length > 0) {
            orders = new ArrayList<>(Arrays.asList(rolePageRequest.getOrders()));
        }
        String likeRoleName = SQLUtils.escapeLike(rolePageRequest.getName());
        ListObjServiceResult result = listWithUserCnt(likeRoleName, orders, opRolePermCheckService.getExcludeRoleIdsByLoginUserForSearchingRoles(), rolePageRequest.getPage(), rolePageRequest.getPageSize());

        return result;
    }

    /**
     * getRoleDto
     *
     * @param id
     * @return
     */
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

    /**
     * getRolePermDto
     *
     * @param id
     * @return
     */
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

    /**
     * add
     *
     * @param createRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleModel add(RoleRequest createRequest) {
        ParamCheck.notNull(createRequest, AuthBasei18nCode.REQUEST_PARAMS);

        RoleModel roleModel = new RoleModel();
        BeanUtils.copyProperties(createRequest, roleModel);

        createCheck(roleModel);

        if (StringUtils.isBlank(roleModel.getIdentifier())) {
            // 如果ident为空，则随机生成一个
            roleModel.setIdentifier(IdUtils.globalId());
        }

        ObjServiceResult<RoleModel> result = create(roleModel);
        ResultUtils.throwExceptionIfError(result, AuthErrorCode.OP_ERROR);

        RoleModel roleCreated = result.getSource().getObj();
        String roleId = roleCreated.getId();
        log.info("role created id:{}", roleId);

        // 保存角色-权限关系
        List<String> targetIds = createRequest.getTargetIds();
        if (CollectionUtils.isNotEmpty(targetIds)) {
            // 对 targetIds 去重
            TreeSet<String> set = new TreeSet<>(targetIds);

            for (String id : set) {
                boolean isTargetExist = targetService.exists(id);
                if (!isTargetExist) {
                    log.error("target id:{} not existed!", id);
                    throw new MpRuntimeException(AuthErrorCode.TARGET_NOT_EXISTED.format(id));
                }
                RolePermissionModel rolePermissionModel = new RolePermissionModel();
                rolePermissionModel.setOwner(roleId);
                rolePermissionModel.setTarget(id);
                // 如果保存错误，抛出异常
                ObjServiceResult re = rolePermissionService.create(rolePermissionModel);
                ResultUtils.throwExceptionIfError(re, AuthErrorCode.ROLE_TARGET_ADD_ERROR);
            }
        }

        return roleCreated;
    }


    /**
     * modify
     *
     * @param modifyRequest
     * @return
     */
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


        // todo 清除原来角色绑定的权限？
        boolean cleanSuccess = rolePermissionService.deleteByRoleId(roleId);
        if (!cleanSuccess) {
            throw new MpRuntimeException(AuthErrorCode.ROLE_TARGET_DELETE_ERROR);
        }

        // 保存角色-权限关系
        if (CollectionUtils.isNotEmpty(modifyRequest.getTargetIds())) {
            for (String id : modifyRequest.getTargetIds()) {
                boolean isTargetExist = targetService.exists(id);
                if (!isTargetExist) {
                    log.error("target id:{} not existed!", id);
                    throw new MpRuntimeException(AuthErrorCode.TARGET_NOT_EXISTED.format(id));
                }
                RolePermissionModel rolePermissionModel = new RolePermissionModel();
                rolePermissionModel.setOwner(roleId);
                rolePermissionModel.setTarget(id);
                // 如果保存错误，抛出异常
                ObjServiceResult re = rolePermissionService.create(rolePermissionModel);
                ResultUtils.throwExceptionIfError(re, AuthErrorCode.ROLE_TARGET_ADD_ERROR);
            }
        }

        userCacheService.evictAllUserCache();
        userCacheService.evictAllTargetsCache();
        messageSender.send(Collections.singletonList(roleId), AuthConstant.MSG_TYPE_ROLE_MODIFY, AuthConstant.ROUTING_KEY_ROLE_MODIFY, null);
        return updateResult.getSource().getObj();
    }

    /**
     * getSystemUnVisibleRoleIds
     *
     * @return
     */
    @Override
    public List<String> getSystemUnVisibleRoleIds() {
        List<RoleModel> models = getModelMapper().list(new HumanCondition().filterEqual(RoleModel::getVisible, false));
        if (CollectionUtils.isEmpty(models)) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (RoleModel m : models) {
            String id = m.getId();
            list.add(id);
        }
        return list;
    }


    /**
     * roleModel2RoleDto
     *
     * @param roleModel
     * @return
     */
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


    /**
     * roleModel2RolePermDto
     *
     * @param roleModel
     * @return
     */
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

    /**
     * getRoleTargets
     *
     * @param roleModel
     * @return
     */
    private List<TargetModel> getRoleTargets(RoleModel roleModel) {
        List<TargetModel> targets = new ArrayList<>();
        if (Objects.equals(roleModel.getId(), AuthConstant.ROLE_ADMIN_ID)) {
            // 管理员特殊处理 直接查询该租户下所有权限返回
            targets = targetService.listAll(AuthConstant.DEFAULT_TENANT, null);
        } else {
            targets = rolePermissionService.listTargetsByRoleIds(Arrays.asList(roleModel.getId()));
        }
        return targets;
    }

    /**
     * targetModel2PermissionDto
     *
     * @param targets
     * @return
     */
    private List<PermissionBo> targetModel2PermissionDto(List<TargetModel> targets) {
        List<PermissionBo> permissionBos =
                new ArrayList<>();
        for (TargetModel target : targets) {
            PermissionBo permissionBo = new PermissionBo();
            BeanUtils.copyProperties(target, permissionBo);
            permissionBo.setPerm("R/W/X");
            permissionBo.setIsEnabled(1);
            PermissionBo apply = permissionBo;
            permissionBos.add(apply);
        }
        return permissionBos;
    }


    /**
     * deleteReleated
     *
     * @param roleId
     */
    private void deleteReleated(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return;
        }

        // 管理员角色不能删除
        if (Objects.equals(AuthConstant.ROLE_ADMIN_ID, roleId)) {
            log.info("ADMIN ROLE can not delete！");
            throw new MpRuntimeException(AuthErrorCode.ROLE_CANNOT_DELETE_ADMIN);
        }

        // 在系统配置中设置了默认注册用户的角色不能删
        if (Objects.equals(roleId, adminConfigService.getRegisterDefaultRoleId())) {
            log.info("Register default role can not delete！");
            throw new MpRuntimeException(AuthErrorCode.ROLE_CANNOT_DELETE_FOR_REGISTER_DEFAULT);
        }

        List<UserModel> users = userRoleService.listUsersByRoleIdNoRestrict(roleId);
        if (CollectionUtils.isNotEmpty(users)) {
            throw new MpRuntimeException(AuthErrorCode.ROLE_CANNOT_DELETE_WITH_USER_EXISTED);
        }

        boolean deleteRolePermissionSuccess = rolePermissionService.deleteByRoleId(roleId);
        boolean deleteUserRoleSuccess = userRoleService.deleteByRoleId(roleId);

        if (!deleteRolePermissionSuccess || !deleteUserRoleSuccess) {
            log.info("deleteRolePermissionSuccess:{}, deleteUserRoleSuccess:{}", deleteRolePermissionSuccess, deleteUserRoleSuccess);
            throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
        }
    }

    /**
     * createCheck
     *
     * @param roleModel
     */
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

    /**
     * listWithUserCnt
     *
     * @param roleName
     * @param orders
     * @param excludeRoleIds
     * @param page
     * @param pageSize
     * @return
     */
    public ListObjServiceResult listWithUserCnt(String roleName, List<String> orders, List<String> excludeRoleIds, int page, int pageSize) {
        HumanCondition roleCondition = new HumanCondition();
        roleCondition.filterEqual(RoleModel::getVisible, true);
        if (CollectionUtils.isNotEmpty(excludeRoleIds)) {
            roleCondition.filterNotIn(RoleModel::getId, excludeRoleIds.toArray());
        }
        if (StringUtils.isNotBlank(roleName)) {
            roleCondition.filterContains(RoleModel::getName, roleName);
        }
        if (CollectionUtils.isNotEmpty(orders)) {
            for (String order : orders) {
                roleCondition.orderBy(order);
            }
        }
        int total = getModelMapper().total(roleCondition);
        roleCondition.pagination(page, pageSize);
        List<RoleModel> roleModels = getModelMapper().list(roleCondition);
        List<String> roleIds = new ArrayList<>();
        for (RoleModel model : roleModels) {
            String id = model.getId();
            roleIds.add(id);
        }

        if (CollectionUtils.isEmpty(roleIds)) {
            roleIds.add("");
        }
        HumanCondition userRoleCondition = new HumanCondition()
                .filterIn(UserRoleModel::getRole, roleIds.toArray())
                .select(UserRoleModel::getRole)
                .selectAs(Count.f(UserRoleModel::getUser).distinct(), "userCnt")
                .groupBy(UserRoleModel::getRole);

        List<Map<String, Object>> maps = userRoleMapper.originList(userRoleCondition);
        Map<String, Integer> roleCountMap = new HashMap<>(maps.size());
        for (Map<String, Object> map : maps) {
            roleCountMap.put((String) map.get("role"), ((Long) map.get("userCnt")).intValue());
        }

        List<RoleBo> rolebos = new ArrayList<>();
        for (RoleModel roleModel : roleModels) {
            RoleBo roleBo = new RoleBo();
            BeanUtils.copyProperties(roleModel, roleBo);
            roleBo.setUserCnt(roleCountMap.getOrDefault(roleModel.getId(), 0));
            rolebos.add(roleBo);
        }

        List<TargetModel> allTargets = targetService.listAll(AuthConstant.DEFAULT_TENANT, null);

        for (RoleBo rolebo : rolebos) {
            if (rolebo.getId().equals(AuthConstant.ROLE_ADMIN_ID)) {
                rolebo.setTargets(allTargets);
            }
        }

        List<String> roleBoIds = new ArrayList<>();
        for (RoleBo role : rolebos) {
            if (!CollectionUtils.isEmpty(role.getTargets())) {
                continue;
            }
            String id = role.getId();
            if (id != null) {
                roleBoIds.add(id);
            }
        }

        if (CollectionUtils.isNotEmpty(roleBoIds)) {
            List<TargetRoleBo> targetRoleBos = rolePermissionService.listTargetRoleDtosByRoleIds(roleBoIds);
            if (CollectionUtils.isNotEmpty(targetRoleBos)) {
                for (RoleBo r : rolebos) {
                    List<TargetModel> targetModels = new ArrayList<>();
                    for (TargetRoleBo tr : targetRoleBos) {
                        if (Objects.equals(tr.getRoleId(), r.getId())) {
                            TargetModel targetModel = new TargetModel();
                            BeanUtils.copyProperties(tr, targetModel);
                            targetModels.add(targetModel);
                        }
                    }
                    r.setTargets(targetModels);
                }
            }
        }
        ListObjServiceResult<RoleBo> result = new ListObjServiceResult(new ListObjResult(rolebos, total).asResult()).asResult();
        return result;
    }

}
