package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.repository.OrgPermissionMapper;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.fields.OrgPermissionFields;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrgPermissionServiceImpl
        extends BaseModelServiceImpl<OrgPermissionModel, OrgPermissionMapper, OrgPermissionFields>
        implements OrgPermissionService {


    @Autowired
    private OrgService orgService;

    @Autowired
    public OrgPermissionServiceImpl(OrgPermissionMapper modelMapper, OrgPermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }


    @Override
    @Transactional
    public boolean bindOrgTargets(String orgId, List<String> targetIds) {
        Assert.notNull(orgId, "orgId不能为空");

        var objResult = orgService.get(orgId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            throw new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "org_not_existed");
        }

        // 清除组织权限
        boolean success = cleanOrgTargets(orgId);
        if (!success) {
            throw new MpRuntimeException("清除组织权限失败！");
        }


        if (CollectionUtil.isEmpty(targetIds)) {
            // 完成，直接返回
            return true;
        }

        // 添加组织权限
        targetIds.forEach(tid -> {
            // todo 校验权限是否存在
            OrgPermissionModel orgPerm = new OrgPermissionModel();
            orgPerm.setOwner(orgId);
            orgPerm.setTarget(tid);
            orgPerm.setTenant(AuthConstant.DEFAULT_TENANT);
        });

        return true;


    }


    @Override
    public boolean cleanOrgTargets(String orgId) {
        Assert.notBlank(orgId, "orgId不能为空！");
        getModelMapper().deleteByOrg(orgId);
        return true;
    }


}
