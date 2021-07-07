package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.role.RoleDto;
import cn.cyberpeace.mp.auth.dto.role.RolePageRequest;
import cn.cyberpeace.mp.auth.dto.role.RoleRequest;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.fields.RoleFields;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.IdGenerator;
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

import javax.management.relation.Role;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class RoleServiceImpl extends BaseModelServiceImpl<RoleModel, RoleMapper, RoleFields, QueryParams>
        implements RoleService {

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private TargetService targetService;

    @Autowired
    public RoleServiceImpl(RoleMapper modelMapper, RoleFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public ServiceResult<Boolean> delete(String id) {
        // todo 删除关联用户关系，权限关系
        return super.delete(id);
    }

    @Override
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        // todo 删除关联用户关系，权限关系
        return super.bulkDelete(ids);
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
    public boolean exists(String id) {
        Assert.notBlank(id, "id不能为空！");
        RoleModel roleModel = getModelMapper().get(id);
        return Objects.nonNull(roleModel);
    }

    @Override
    public PageInfo getPageWithUserCnt(RolePageRequest rolePageRequest) {
        PageHelper.startPage(rolePageRequest.getPage(), rolePageRequest.getPageSize());
        List<RoleDto> roleDtos = getModelMapper().listWithUserCnt(rolePageRequest.getName());
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
    @Transactional(rollbackFor = Exception.class)
    public RoleModel add(RoleRequest createRequest) {
        Assert.notNull(createRequest, "roleRequest不能为空！");

        // 保存用户
        RoleModel roleModel = new RoleModel();
        BeanUtils.copyProperties(createRequest, roleModel);

        if (StringUtils.isBlank(roleModel.getIdentifier())) {
            // 如果ident为空，则随机生成一个
            roleModel.setIdentifier(String.valueOf(IdGenerator.generateGlobalId()));
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
                ResultUtils.throwExceptionIfError(re, "保存权限关系失败!");
            });
        }

        return roleCreated;
    }
}
