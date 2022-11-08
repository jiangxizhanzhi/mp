package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.role.RoleDto;
import cn.cyberpeace.mp.auth.dto.role.RolePermDto;
import cn.cyberpeace.mp.auth.dto.target.PermissionDto;
import cn.cyberpeace.mp.auth.dto.target.TargetRoleDto;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.request.role.RolePageRequest;
import cn.cyberpeace.mp.auth.request.role.RoleRequest;
import cn.cyberpeace.mp.auth.service.*;
import cn.cyberpeace.mp.auth.service.fields.RoleFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.SQLUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.IdUtils;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
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
    private TargetService targetService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private OperateRolePermissionService opRolePermCheckService;

    @Autowired
    public RoleServiceImpl(RoleMapper modelMapper, RoleFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean> delete(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);
        // todo 删除关联用户关系，权限关系
        deleteReleated(roleId);
        return super.delete(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        // todo 删除关联用户关系，权限关系

        for (String id : ids) {
            deleteReleated(id);
        }

        return super.bulkDelete(ids);
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
                .filterEqual(RoleModel::getTenant, tenantId);
        List<RoleModel> roles = getModelMapper().list(humanCondition);
        return roles;
    }


    @Override
    public List<RoleModel> listAll() {
        List<String> excludeRoleIds = opRolePermCheckService.getExcludeRoleIdsByLoginUserForSearchingRoles();
        HumanCondition humanCondition = new HumanCondition();
        if (CollectionUtil.isNotEmpty(excludeRoleIds)) {
            humanCondition.filterNotIn(RoleModel::getId, excludeRoleIds.toArray());
        }
        List<RoleModel> roles = getModelMapper().list(humanCondition);
        return roles;
    }

    @Override
    public List<RoleModel> listByOrgId(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RoleModel::getOrg, orgId);
        List<RoleModel> roles = getModelMapper().list(humanCondition);
        return roles;
    }

    @Override
    public RoleModel getByIdent(String ident) {
        ParamCheck.notBlank(ident, AuthBasei18nCode.ROLE_IDENTIFIER);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RoleModel::getIdentifier, ident);
        RoleModel model = getModelMapper().first(humanCondition);
        return model;
    }

    @Override
    @Cacheable(value = CACHE_VAL_ROLE_GETBY_NAME)
    public RoleModel getByName(String name) {
        ParamCheck.notBlank(name, AuthBasei18nCode.ROLE_NAME);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RoleModel::getName, name);
        RoleModel model = getModelMapper().first(humanCondition);
        return model;
    }


    @Override
    @Cacheable(value = CACHE_VAL_ROLE_LISTBY_NAMES)
    public List<RoleModel> listByNames(List<String> names) {
        ParamCheck.notEmpty(names, AuthBasei18nCode.ROLE_NAME);
        HumanCondition humanCondition = new HumanCondition()
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
    public PageInfo getPageWithUserCnt(RolePageRequest rolePageRequest) {
        ArrayList<String> orders = null;
        if (rolePageRequest.getOrders() != null && rolePageRequest.getOrders().length > 0) {
            orders = CollectionUtil.newArrayList(rolePageRequest.getOrders());
        }

        PageHelper.startPage(rolePageRequest.getPage(), rolePageRequest.getPageSize());
        String likeRoleName = SQLUtils.escapeLike(rolePageRequest.getName());
        List<RoleDto> roleDtos = getModelMapper().listWithUserCnt(likeRoleName, orders, opRolePermCheckService.getExcludeRoleIdsByLoginUserForSearchingRoles());
        PageInfo pageInfo = new PageInfo(roleDtos);
        List<TargetModel> allTargets = targetService.listAll(AuthConstant.DEFAULT_TENANT, null);

        roleDtos.forEach(r -> {
            if (r.getId().equals(AuthConstant.ROLE_ADMIN_ID)) {
                r.setTargets(allTargets);
            }
        });

        List<String> roleIds =
        roleDtos.stream().map(r -> {
            if (CollectionUtil.isEmpty(r.getTargets())) {
                return r.getId();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(roleIds)) {
            List<TargetRoleDto> targetRoleDtos = rolePermissionService.listTargetRoleDtosByRoleIds(roleIds);
            if (CollectionUtil.isNotEmpty(targetRoleDtos)) {
                roleDtos.forEach(r -> {
                    List<TargetModel> targetModels = targetRoleDtos.stream().map(tr -> {
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

        return pageInfo;
    }

    @Override
    public RoleDto getRoleDto(String id) {
        ParamCheck.notBlank(id, AuthBasei18nCode.ROLE_ID);
        List<String> excludeRoleIds = opRolePermCheckService.getExcludeRoleIdsByLoginUserForSearchingRoles();
        if (excludeRoleIds.contains(id.trim())) {
            return null;
        }
        RoleModel roleModel = getModelMapper().get(id);
        RoleDto roleDto = roleModel2RoleDto(roleModel);
        return roleDto;
    }

    @Override
    public RolePermDto getRolePermDto(String id) {
        ParamCheck.notBlank(id, AuthBasei18nCode.ROLE_ID);
        List<String> excludeRoleIds = opRolePermCheckService.getExcludeRoleIdsByLoginUserForSearchingRoles();
        if (excludeRoleIds.contains(id.trim())) {
            return null;
        }
        RoleModel roleModel = getModelMapper().get(id);
        RolePermDto rolePermDto = roleModel2RolePermDto(roleModel);
        return rolePermDto;
    }

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
        if (CollectionUtil.isNotEmpty(createRequest.getTargetIds())) {
            createRequest.getTargetIds().forEach(id -> {
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

        log.info("modify roleid:{}", roleId);
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
        if (CollectionUtil.isNotEmpty(modifyRequest.getTargetIds())) {
            modifyRequest.getTargetIds().forEach(id -> {
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
            });
        }

        return updateResult.getSource().getObj();
    }


    private RoleDto roleModel2RoleDto(RoleModel roleModel) {
        if (Objects.isNull(roleModel)) {
            return null;
        }

        RoleDto roleDto = new RoleDto();
        cn.cyberpeace.mp.base.util.basic.BeanUtils.copyProperties(roleModel, roleDto);

        List<TargetModel> targets = getRoleTargets(roleModel);

        roleDto.setTargets(targets);
        return roleDto;
    }


    private RolePermDto roleModel2RolePermDto(RoleModel roleModel) {
        if (Objects.isNull(roleModel)) {
            return null;
        }

        RolePermDto rolePermDto = new RolePermDto();
        cn.cyberpeace.mp.base.util.basic.BeanUtils.copyProperties(roleModel, rolePermDto);

        List<TargetModel> targets = getRoleTargets(roleModel);

        rolePermDto.setTargets(targetModel2PermissionDto(targets));
        return rolePermDto;
    }

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

    private List<PermissionDto> targetModel2PermissionDto(List<TargetModel> targets) {
        List<PermissionDto> permissionDtos =
                targets.stream().map(t -> {
                    PermissionDto permissionDto = new PermissionDto();
                    BeanUtils.copyProperties(t, permissionDto);
                    permissionDto.setPerm("R/W/X");
                    permissionDto.setIsEnabled(1);
                    return permissionDto;
                }).collect(Collectors.toList());
        return permissionDtos;
    }


    private void deleteReleated(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return;
        }

        // 管理员角色不能删除
        if (Objects.equals(AuthConstant.ROLE_ADMIN_ID, roleId)) {
            log.info("ADMIN ROLE can not delete！");
            throw new MpRuntimeException(AuthErrorCode.ROLE_CANNOT_DELETE_ADMIN);
        }

        List<UserModel> users = userRoleService.listUsersByRoleIdNoRestrict(roleId);
        if (CollectionUtil.isNotEmpty(users)) {
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

}
