package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.OrgPermissionMapper;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.fields.OrgPermissionFields;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrgPermissionServiceImpl
        extends BaseModelServiceImpl<OrgPermissionModel, OrgPermissionMapper, OrgPermissionFields, QueryParams>
        implements OrgPermissionService {


    @Autowired
    private OrgService orgService;

    @Autowired
    private OrgPermissionService orgPermissionService;


    @Autowired
    public OrgPermissionServiceImpl(OrgPermissionMapper modelMapper, OrgPermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public Set<TargetModel> listTargetsByOrgId(String orgId) {
        Assert.notNull(orgId, "orgId不能为空");

//        var objResult = orgService.get(orgId).getSource();
//        if (objResult == null || objResult.getObj() == null) {
//            throw new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "org_not_existed");
//        }
        List<TargetModel> targets = getModelMapper().listByOrgId(orgId);
        return new HashSet<>(targets);
    }

    @Override
    public Set<TargetModel> listTargetsByOrgIdIncludeAllParentsOrg(String orgId) {
        OrgDto orgDto = orgService.getHierarchicalOrgsByOrgId(orgId);
        if (orgDto == null) {
            return new HashSet<>();
        }
        Set<TargetModel> targetSet = new HashSet<>();
        recursionTargetsByOrgDto(targetSet, orgDto);

        return targetSet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindOrgTargets(String orgId, List<String> targetIds, boolean cleanOldTargets) {
        Assert.notNull(orgId, "orgId不能为空");

        var result = orgService.get(orgId);
        ResultUtils.throwExceptionIfError(result, AuthConstant.ERROR_CODE_PREFIX + "org_not_existed");

        if (cleanOldTargets) {
            // 清除组织权限
            boolean success = deleteByOrgId(orgId);
            if (!success) {
                throw new MpRuntimeException("清除组织权限失败！");
            }
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
            var re = orgPermissionService.create(orgPerm);
            ResultUtils.throwExceptionIfError(re, "创建组织权限关系失败！");
        });

        return true;


    }


    @Override
    public boolean deleteByOrgId(String orgId) {
        Assert.notBlank(orgId, "orgId不能为空！");
        getModelMapper().deleteByOrg(orgId);
        return true;
    }


    // 递归查询组织绑定的权限（如果有父级组织，则继续查找）
    private void recursionTargetsByOrgDto(Set<TargetModel> set, OrgDto orgDto) {
        if (orgDto == null) {
            return;
        }
        Set<TargetModel> tset = listTargetsByOrgId(orgDto.getParent());
//        Set<TargetModel> targetSet = new HashSet<>();
//        for (TargetModel target : targets) {
//            targetSet.add(target);
//        }
        set.addAll(tset);
        recursionTargetsByOrgDto(set, orgDto.getParentOrg());
    }


}
