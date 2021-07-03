package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.fields.RoleFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends BaseModelServiceImpl<RoleModel, RoleMapper, RoleFields, QueryParams>
        implements RoleService {

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
}
