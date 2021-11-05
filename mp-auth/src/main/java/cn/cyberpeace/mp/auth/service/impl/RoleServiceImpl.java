package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.role.RoleDto;
import cn.cyberpeace.mp.auth.dto.role.RolePageRequest;
import cn.cyberpeace.mp.auth.dto.role.RolePermDto;
import cn.cyberpeace.mp.auth.dto.role.RoleRequest;
import cn.cyberpeace.mp.auth.dto.target.PermissionDto;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.fields.RoleFields;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.SQLUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.IdUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
public class RoleServiceImpl extends BaseModelServiceImpl<RoleModel, RoleMapper, RoleFields, QueryParams>
        implements RoleService {

    @Autowired
    private RolePermissionModelService rolePermissionService;

    @Autowired
    private TargetService targetService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    public RoleServiceImpl(RoleMapper modelMapper, RoleFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean> delete(String roleId) {
        Assert.notBlank(roleId, "id不能为空！");
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
        Assert.notBlank(tenantId, "tenantId不能为空！");
        List<RoleModel> roles = getModelMapper().listByTenantId(tenantId);
        return roles;
    }


    @Override
    public List<RoleModel> listAll() {
        List<RoleModel> roles = getModelMapper().listAll();
        return roles;
    }

    @Override
    public List<RoleModel> listByOrgId(String orgId) {
        Assert.notBlank(orgId, "orgId不能为空！");
        List<RoleModel> roles = getModelMapper().listByOrgId(orgId);
        return roles;
    }

    @Override
    public RoleModel getByIdent(String ident) {
        Assert.notBlank(ident, "identifier不能为空！");
        RoleModel roleModel = getModelMapper().selectByIdent(ident);
        return roleModel;
    }

    @Override
    public RoleModel getByName(String name) {
        Assert.notBlank(name, "name不能为空！");
        RoleModel roleModel = getModelMapper().selectByName(name);
        return roleModel;
    }

    @Override
    public boolean exists(String id) {
        Assert.notBlank(id, "id不能为空！");
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
        List<RoleDto> roleDtos = getModelMapper().listWithUserCnt(likeRoleName, orders);
        PageInfo pageInfo = new PageInfo(roleDtos);

        roleDtos.forEach(r -> {
            if (r.getIdentifier().equals(AuthConstant.ROLE_ADMIN)) {
                r.setTargets(targetService.listAll(AuthConstant.DEFAULT_TENANT, null));
            } else {
                List<TargetModel> targetModels = rolePermissionService.listTargetsByRoleId(r.getId());
                r.setTargets(targetModels);
            }
        });

        return pageInfo;
    }

    @Override
    public RoleDto getRoleDto(String id) {
        Assert.notBlank(id, "id不能为空！");
        RoleModel roleModel = getModelMapper().get(id);
        RoleDto roleDto = roleModel2RoleDto(roleModel);
        return roleDto;
    }

    @Override
    public RolePermDto getRolePermDto(String id) {
        Assert.notBlank(id, "id不能为空！");
        RoleModel roleModel = getModelMapper().get(id);
        RolePermDto rolePermDto = roleModel2RolePermDto(roleModel);
        return rolePermDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleModel add(RoleRequest createRequest) {
        Assert.notNull(createRequest, "createRequest不能为空！");

        RoleModel roleModel = new RoleModel();
        BeanUtils.copyProperties(createRequest, roleModel);

        createCheck(roleModel);

        if (StringUtils.isBlank(roleModel.getIdentifier())) {
            // 如果ident为空，则随机生成一个
            roleModel.setIdentifier(IdUtils.globalId());
        }

        ObjServiceResult<RoleModel> result = create(roleModel);
        ResultUtils.throwExceptionIfError(result, "创建角色失败!");

        RoleModel roleCreated = result.getSource().getObj();
        String roleId = roleCreated.getId();
        log.info("role created id:{}", roleId);

        // 保存角色-权限关系
        if (CollectionUtil.isNotEmpty(createRequest.getTargetIds())) {
            createRequest.getTargetIds().forEach(id -> {
                boolean isTargetExist = targetService.exists(id);
                if (!isTargetExist) {
                    log.error("权限id:{}不存在", id);
                    throw new MpRuntimeException("权限id：" + id + "不存在！");
                }
                RolePermissionModel rolePermissionModel = new RolePermissionModel();
                rolePermissionModel.setOwner(roleId);
                rolePermissionModel.setTarget(id);
                // 如果保存错误，抛出异常
                ObjServiceResult re = rolePermissionService.create(rolePermissionModel);
                ResultUtils.throwExceptionIfError(re, "保存角色权限关系失败!");
            });
        }

        return roleCreated;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleModel modify(RoleRequest modifyRequest) {
        Assert.notNull(modifyRequest, "modifyRequest不能为空！");
        String roleId = modifyRequest.getId();
        Assert.notBlank(roleId, "id不能为空！");
        log.info("roleid:{}", roleId);

        var result = get(roleId);
        ResultUtils.throwExceptionIfError(result, AuthConstant.ERROR_CODE_PREFIX + "role_not_existed");

        RoleModel roleModel = result.getSource().getObj();
        if (Objects.equals(AuthConstant.ROLE_ADMIN, roleModel.getIdentifier())) {
            log.info("管理员角色不需要更新！");
            return roleModel;
        }

        RoleModel roleUpdate = new RoleModel();
        BeanUtils.copyProperties(modifyRequest, roleUpdate);
        ObjServiceResult<RoleModel> updateResult = update(roleUpdate);
        ResultUtils.throwExceptionIfError(updateResult, "修改角色失败！");


        // todo 清除原来角色绑定的权限？
        boolean cleanSuccess = rolePermissionService.deleteByRoleId(roleId);
        if (!cleanSuccess) {
            throw new MpRuntimeException("清除原来角色绑定的权限关系失败！");
        }

        // 保存角色-权限关系
        if (CollectionUtil.isNotEmpty(modifyRequest.getTargetIds())) {
            modifyRequest.getTargetIds().forEach(id -> {
                boolean isTargetExist = targetService.exists(id);
                if (!isTargetExist) {
                    log.error("权限id:{}不存在", id);
                    throw new MpRuntimeException("权限id：" + id + "不存在！");
                }
                RolePermissionModel rolePermissionModel = new RolePermissionModel();
                rolePermissionModel.setOwner(roleId);
                rolePermissionModel.setTarget(id);
                // 如果保存错误，抛出异常
                ObjServiceResult re = rolePermissionService.create(rolePermissionModel);
                ResultUtils.throwExceptionIfError(re, "保存角色权限关系失败!");
            });
        }

        return updateResult.getSource().getObj();
    }


    private RoleDto roleModel2RoleDto(RoleModel roleModel) {
        if (Objects.isNull(roleModel)) {
            return null;
        }

        RoleDto roleDto = new RoleDto();
        cn.cyberpeace.mp.base.util.BeanUtils.copyProperties(roleModel, roleDto);

        List<TargetModel> targets = getRoleTargets(roleModel);

        roleDto.setTargets(targets);
        return roleDto;
    }


    private RolePermDto roleModel2RolePermDto(RoleModel roleModel) {
        if (Objects.isNull(roleModel)) {
            return null;
        }

        RolePermDto rolePermDto = new RolePermDto();
        cn.cyberpeace.mp.base.util.BeanUtils.copyProperties(roleModel, rolePermDto);

        List<TargetModel> targets = getRoleTargets(roleModel);

        rolePermDto.setTargets(targetModel2PermissionDto(targets));
        return rolePermDto;
    }

    private List<TargetModel> getRoleTargets(RoleModel roleModel) {
        List<TargetModel> targets = new ArrayList<>();
        if (Objects.equals(roleModel.getIdentifier(), AuthConstant.ROLE_ADMIN)) {
            // 管理员特殊处理 直接查询该租户下所有权限返回
            targets = targetService.listAll(AuthConstant.DEFAULT_TENANT, null);
        } else {
            targets = rolePermissionService.listTargetsByRoleId(roleModel.getId());
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
        RoleModel roleModel = getModelMapper().get(roleId);
        if (Objects.equals(AuthConstant.ROLE_ADMIN, roleModel.getIdentifier())) {
            log.info("管理员角色不能删除！");
            throw new MpRuntimeException("管理员角色不能删除！");
        }

        List<UserModel> users = userRoleService.listUsersByRoleId(roleId);
        if (CollectionUtil.isNotEmpty(users)) {
            throw new MpRuntimeException("当前角色下有用户，不允许删除！");
        }


        boolean deleteRolePermissionSuccess = rolePermissionService.deleteByRoleId(roleId);
        boolean deleteUserRoleSuccess = userRoleService.deleteByRoleId(roleId);

        if (!deleteRolePermissionSuccess || !deleteUserRoleSuccess) {
            log.info("deleteRolePermissionSuccess:{}, deleteUserRoleSuccess:{}", deleteRolePermissionSuccess, deleteUserRoleSuccess);
            throw new MpRuntimeException("删除失败！");
        }
    }

    private void createCheck(RoleModel roleModel) {
        if (StringUtils.isNotBlank(roleModel.getName())) {
            if (getByName(roleModel.getName()) != null) {
                throw new MpRuntimeException("角色名已存在！[" + roleModel.getName() + "]");
            }
        }

        if (Objects.equals(AuthConstant.ROLE_ADMIN, roleModel.getIdentifier())) {
            throw new MpRuntimeException("角色编号不能是ADMIN");
        }
    }

}
