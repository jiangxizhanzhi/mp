package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.target.TargetDto;
import cn.cyberpeace.mp.auth.dto.target.TargetType;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.TargetMapper;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.fields.TargetFields;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class TargetServiceImpl extends BaseModelServiceImpl<TargetModel, TargetMapper, TargetFields, QueryParams>
        implements TargetService {

    @Autowired
    private QueryChildTargetOperation queryChildTargetOperation;

    @Autowired
    private QueryParentTargetOperation queryParentTargetOperation;

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
        Assert.notBlank(identifier);
        return getModelMapper().first(new HumanCondition().filterEqual("identifier", identifier));
    }

//    @Override
//    public List<TargetModel> listByTenantId(String tenantId) {
//        List<TargetModel> targets = getModelMapper().listByTenantId(tenantId);
//        return targets;
//    }

    @Override
    public boolean exists(String id) {
        Assert.notBlank(id, "id不能为空！");
        TargetModel targetModel = getModelMapper().get(id);
        return Objects.nonNull(targetModel);
    }

    @Override
    public List<TargetModel> listByParent(String parentId) {
        Assert.notBlank(parentId, "parentId不能为空！");
        List<TargetModel> list = getModelMapper().listByParent(parentId);
        return list;
    }

    @Override
    public ObjServiceResult<TargetModel> create(TargetModel obj, String[] fields) {
        check(obj);
        return super.create(obj, fields);
    }

    @Override
    public List<TargetDto> getTree(String parentId, Integer type) {
        if (Objects.isNull(type)) {
            // 默认查询菜单树
            type = TargetModel.TYPE_MENU;
        }

        if (StringUtils.isBlank(parentId)) {
            // parent为空，则查询所有权限
            List<TargetModel> targets = listAll(AuthConstant.DEFAULT_TENANT, type);
            if (CollectionUtil.isEmpty(targets)) {
                return new ArrayList<>();
            }
            List<TargetDto> tree = toTree(targets);

            // 父节点根据pk做排序
//            tree.sort((o1, o2) -> o1.getPk() - o2.getPk());
            sortTree(tree);

            return tree;
        }

        // 根据parentId查询单个权限树
        TargetModel parentTarget = getModelMapper().get(parentId);
        if (parentTarget == null) {
            log.info("target:{}不存在!", parentId);
            throw new MpRuntimeException(AuthErrorCode.TARGET_NOT_EXISTED);
        }

        TargetDto targetDto = new TargetDto();
        BeanUtils.copyProperties(parentTarget, targetDto);
        targetDto.execute(queryChildTargetOperation);

        return CollectionUtil.toList(targetDto);
    }

    public List<TargetModel> listAll(String tenant, Integer type) {
        List<TargetModel> targets = getModelMapper().listAll(tenant, type);
        return targets;
    }

    public List<TargetModel> listByMultiTypes(String tenant, Integer[] types) {
        List<TargetModel> targets = getModelMapper().listByMultiTypes(tenant, types);
        return targets;
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



    private List<TargetDto> toTree(List<TargetModel> targets) {
        Assert.notEmpty(targets, "targets should not be empty!");
        Map<String, List<TargetModel>> map = new HashMap<>();
        Map<String, TargetDto> mapTmp = new HashMap<>();

        //Save all nodes to a map
        for (TargetModel targetm : targets) {
            TargetDto targetDto = new TargetDto();
            BeanUtils.copyProperties(targetm, targetDto);
            mapTmp.put(targetDto.getId(), targetDto);
        }

        //loop and assign parent/child relationships
        for (Map.Entry<String, TargetDto> entry : mapTmp.entrySet()) {
            TargetDto current = entry.getValue();
            String parentId = current.getParent();

            if (StringUtils.isNotBlank(parentId)) {
                TargetDto parent = mapTmp.get(parentId);
                if (parent != null) {
                    //                    current.(parent);
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(current);
                    mapTmp.put(parentId, parent);
                }
            }

        }

        List<TargetDto> parentTargets = new ArrayList<>();
        //get the root
        for (TargetDto node : mapTmp.values()) {
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

        if(!deleteUserPermissionSuccess || !deleteOrgPermissionSuccess || !deleteRolePermissionSuccess) {
            throw new MpRuntimeException("删除失败！");
        }
    }

    // 权限树排序
    private void sortTree(List<TargetDto> targets) {
        targets.forEach(t -> {
            if (CollectionUtil.isNotEmpty(t.getChildren())) {
                sortTree(t.getChildren());
            }
        });

        targets.sort(Comparator.comparingInt(BaseModel::getPk));
    }

}
