package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.OrgPermissionMapper;
import cn.cyberpeace.mp.auth.repository.TargetMapper;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.fields.OrgPermissionFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class OrgPermissionServiceImpl
        extends BaseModelServiceImpl<OrgPermissionModel, OrgPermissionMapper, OrgPermissionFields, QueryParams>
        implements OrgPermissionService {


    @Autowired
    private OrgService orgService;

    @Autowired
    private OrgPermissionService orgPermissionService;

    @Autowired
    private TargetMapper targetMapper;

    @Autowired
    public OrgPermissionServiceImpl(OrgPermissionMapper modelMapper, OrgPermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public Set<TargetModel> listTargetsByOrgId(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        HumanCondition orgPermissionCondition = new HumanCondition()
                .filterEqual(OrgPermissionModel::getOwner, orgId)
                .select(OrgPermissionModel::getTarget);
        List<OrgPermissionModel> orgPermissionModels = getModelMapper().list(orgPermissionCondition);
        List<String> targetIds = orgPermissionModels.stream()
                .map(OrgPermissionModel::getTarget)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(targetIds)) {
            return new HashSet<>();
        }
        HumanCondition targetCondition = new HumanCondition()
                .filterIn(TargetModel::getId, targetIds.toArray())
                .filterEqual(TargetModel::getVisible, 1)
                .distinct();
        List<TargetModel> targets = targetMapper.list(targetCondition);
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
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);

        var result = orgService.get(orgId);
        ResultUtils.throwExceptionIfError(result, AuthErrorCode.ORG_NOT_EXISTED);

        if (cleanOldTargets) {
            
            boolean success = deleteByOrgId(orgId);
            if (!success) {
                throw new MpRuntimeException(AuthErrorCode.ORG_TARGET_DELETE_ERROR);
            }
        }

        if (CollectionUtil.isEmpty(targetIds)) {
            
            return true;
        }

        
        targetIds.forEach(tid -> {
            
            OrgPermissionModel orgPerm = new OrgPermissionModel();
            orgPerm.setOwner(orgId);
            orgPerm.setTarget(tid);
            orgPerm.setTenant(AuthConstant.DEFAULT_TENANT);
            var re = orgPermissionService.create(orgPerm);
            ResultUtils.throwExceptionIfError(re, AuthErrorCode.ORG_TARGET_ADD_ERROR);
        });

        return true;


    }


    @Override
    public boolean deleteByOrgId(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(OrgPermissionModel::getOwner, orgId);
        getModelMapper().filterDelete(humanCondition);
        return true;
    }

    @Override
    public boolean deleteByTargetId(String targetId) {
        ParamCheck.notBlank(targetId, AuthBasei18nCode.TARGET_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(OrgPermissionModel::getTarget, targetId);
        getModelMapper().filterDelete(humanCondition);
        return true;
    }


    
    private void recursionTargetsByOrgDto(Set<TargetModel> set, OrgDto orgDto) {
        if (orgDto == null || StringUtils.isBlank(orgDto.getParent())) {
            return;
        }
        Set<TargetModel> tset = listTargetsByOrgId(orgDto.getParent());
        set.addAll(tset);
        recursionTargetsByOrgDto(set, orgDto.getParentOrg());
    }


}
