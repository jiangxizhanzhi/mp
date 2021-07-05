package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.role.RoleDto;
import cn.cyberpeace.mp.auth.dto.role.RolePageRequest;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.fields.RoleFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
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
    public PageInfo getPage(RolePageRequest rolePageRequest) {
        PageHelper.startPage(rolePageRequest.getPage(), rolePageRequest.getPageSize());
        List<RoleDto> roleDtos = getModelMapper().listWithUserCnt(rolePageRequest.getName());
        PageInfo pageInfo = new PageInfo(roleDtos);

        roleDtos.forEach(r -> {
            if (r.getIdentifier().equals(AuthConstant.ADMIN)) {
                r.setTargets(targetService.listByTenantId(AuthConstant.DEFAULT_TENANT));
            } else {
                List<TargetModel> targetModels = rolePermissionService.listTargetsByRoleId(r.getId());
                r.setTargets(targetModels);
            }
        });

        return pageInfo;
    }
}
