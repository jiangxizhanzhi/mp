package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.bo.target.TargetType;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.TargetMapper;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.fields.TargetFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Slf4j
public class TargetServiceImpl extends BaseModelServiceImpl<TargetModel, TargetMapper, TargetFields, QueryParams>
        implements TargetService {

    public static final String CACHE_VAL_TARGETS= "TARGETS_CACHE";

    @Autowired
    private QueryChildTargetOperation queryChildTargetOperation;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private OrgPermissionService orgPermissionService;

    @Autowired
    private RolePermissionModelService rolePermissionService;

    @Autowired
    public TargetServiceImpl(TargetMapper modelMapper, TargetFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean> delete(String id) {
        deleteReleated(id);
        return super.delete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        for (String id : ids) {
            deleteReleated(id);
        }
        return super.bulkDelete(ids);
    }

    @Override
    public ObjServiceResult<TargetModel> update(TargetModel obj, String[] fields) {
        if (StringUtils.isBlank(obj.getIdentifier())) {
            obj.setIdentifier(null);
        }
        return super.update(obj, fields);
    }

    @Override
    public TargetModel getByIdentifier(String identifier) {
        ParamCheck.notBlank(identifier, AuthBasei18nCode.TARGET_IDENTIFIER);
        return getModelMapper().first(new HumanCondition().filterEqual("identifier", identifier));
    }







    @Override
    public boolean exists(String id) {
        ParamCheck.notBlank(id, AuthBasei18nCode.TARGET_ID);
        TargetModel targetModel = getModelMapper().get(id);
        return Objects.nonNull(targetModel);
    }

    @Override
    public List<TargetModel> listByParent(String parentId) {
        ParamCheck.notBlank(parentId, AuthBasei18nCode.TARGET_PARENT);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(TargetModel::getParent, parentId)
                .filterEqual(TargetModel::getVisible, AuthConstant.TARGET_FIELD_VALUE_VISIBLE_1);
        List<TargetModel> targetModels = getModelMapper().list(humanCondition);
        return targetModels;
    }

    @Override
    public ObjServiceResult<TargetModel> create(TargetModel obj, String[] fields) {
        check(obj);
        return super.create(obj, fields);
    }

    @Override
    public List<TargetBo> getTree(String parentId, Integer type) {
        if (Objects.isNull(type)) {
            
            type = TargetModel.TYPE_MENU;
        }

        if (StringUtils.isBlank(parentId)) {
            
            List<TargetModel> targets = listAll(AuthConstant.DEFAULT_TENANT, type);
            if (CollectionUtil.isEmpty(targets)) {
                return new ArrayList<>();
            }
            List<TargetBo> tree = toTree(targets);

            

            sortTree(tree);

            return tree;
        }

        
        TargetModel parentTarget = getModelMapper().get(parentId);
        if (parentTarget == null) {
            log.info("target:{} not existed!", parentId);
            throw new MpRuntimeException(AuthErrorCode.TARGET_NOT_EXISTED.format(parentId));
        }

        TargetBo targetBo = new TargetBo();
        BeanUtils.copyProperties(parentTarget, targetBo);
        targetBo.execute(queryChildTargetOperation);

        return CollectionUtil.toList(targetBo);
    }

    @Override
    @Cacheable(cacheManager = AuthCacheConfig.CACHE_MANAGER_LONG, value = CACHE_VAL_TARGETS)
    public List<TargetModel> listAll(String tenant, Integer type) {
        HumanCondition humanCondition = new HumanCondition();
        if (Objects.nonNull(type)) {
            humanCondition.filterEqual(TargetModel::getType, type);
        }
        if (StringUtils.isNotBlank(tenant)) {
            humanCondition.filterEqual(TargetModel::getTenant, tenant);
        }
        humanCondition.filterEqual(TargetModel::getVisible, AuthConstant.TARGET_FIELD_VALUE_VISIBLE_1);
        List<TargetModel> targets = getModelMapper().list(humanCondition);
        return targets;
    }

    @Override
    public List<TargetModel> listAllIgnoreVisible(String tenant, Integer type) {
        HumanCondition humanCondition = new HumanCondition();
        if (Objects.nonNull(type)) {
            humanCondition.filterEqual(TargetModel::getType, type);
        }
        if (StringUtils.isNotBlank(tenant)) {
            humanCondition.filterEqual(TargetModel::getTenant, tenant);
        }
        List<TargetModel> targets = getModelMapper().list(humanCondition);
        return targets;
    }

    @Override
    public List<TargetModel> listByMultiTypes(String tenant, Integer[] types) {
        HumanCondition humanCondition = new HumanCondition();
        if (Objects.nonNull(types) && types.length > 0) {
            humanCondition.filterIn(TargetModel::getType, types);
        }
        if (StringUtils.isNotBlank(tenant)) {
            humanCondition.filterEqual(TargetModel::getTenant, tenant);
        }
        humanCondition.filterEqual(TargetModel::getVisible, AuthConstant.TARGET_FIELD_VALUE_VISIBLE_1);
        List<TargetModel> targets = getModelMapper().list(humanCondition);
        return targets;
    }

    @Override
    public boolean deleteAll() {
        HumanCondition humanCondition = new HumanCondition()
                .filterGreaterThan(TargetModel::getPk, AuthConstant.TARGET_FIELD_VALUE_PK_0);
        int result = getModelMapper().filterDelete(humanCondition);
        log.info("deleted {} rows", result);
        return true;
    }

    private void check(TargetModel target) {
        boolean supportedType = false;
        if (target.getType() != null) {
            for (TargetType ty : TargetType.values()) {
                if (target.getType() == ty.getType()) {
                    supportedType = true;
                    break;
                }
            }
        }

        if (!supportedType) {
            throw new MpRuntimeException(AuthErrorCode.TARGET_TYPE_NOT_SUPPORT);
        }

    }



    private List<TargetBo> toTree(List<TargetModel> targets) {
        ParamCheck.notEmpty(targets, AuthBasei18nCode.TARGET_LIST);
        Map<String, List<TargetModel>> map = new HashMap<>();
        Map<String, TargetBo> mapTmp = new HashMap<>();

        
        for (TargetModel targetm : targets) {
            TargetBo targetBo = new TargetBo();
            BeanUtils.copyProperties(targetm, targetBo);
            mapTmp.put(targetBo.getId(), targetBo);
        }

        
        for (Map.Entry<String, TargetBo> entry : mapTmp.entrySet()) {
            TargetBo current = entry.getValue();
            String parentId = current.getParent();

            if (StringUtils.isNotBlank(parentId)) {
                TargetBo parent = mapTmp.get(parentId);
                if (parent != null) {
                    
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(current);
                    mapTmp.put(parentId, parent);
                }
            }

        }

        List<TargetBo> parentTargets = new ArrayList<>();
        
        for (TargetBo node : mapTmp.values()) {
            if(StringUtils.isBlank(node.getParent())) {
                parentTargets.add(node);
            }
        }

        return parentTargets;
    }


    private void deleteReleated(String targetId) {
        if (StringUtils.isBlank(targetId)) {
            return;
        }

        boolean deleteUserPermissionSuccess = userPermissionService.deleteByTargetId(targetId);
        boolean deleteOrgPermissionSuccess = orgPermissionService.deleteByTargetId(targetId);
        boolean deleteRolePermissionSuccess = rolePermissionService.deleteByTargetId(targetId);

        if (!deleteUserPermissionSuccess || !deleteOrgPermissionSuccess || !deleteRolePermissionSuccess) {
            log.error("delete related failed！targetId:{}", targetId);
            throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
        }
    }

    
    private void sortTree(List<TargetBo> targets) {
        targets.forEach(t -> {
            if (CollectionUtil.isNotEmpty(t.getChildren())) {
                sortTree(t.getChildren());
            }
        });

        targets.sort(Comparator.comparingInt(BaseModel::getPk));
    }

    
    private List<TargetModel> listByTenantId(String tenantId) {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(TargetModel::getTenant, tenantId)
                .filterEqual(TargetModel::getVisible, AuthConstant.TARGET_FIELD_VALUE_VISIBLE_1);
        List<TargetModel> targetModels = getModelMapper().list(humanCondition);
        return targetModels;

    }

}
