package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.MenuTemplateModel;
import cn.cyberpeace.mp.auth.model.MenuTemplateRoleModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.MenuTemplateMapper;
import cn.cyberpeace.mp.auth.repository.MenuTemplateRoleMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service("templateTargetServiceImpl")
@Slf4j
public class TemplateTargetServiceImpl implements TargetService {

    @Autowired
    private MenuTemplateMapper menuTemplateMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private MenuTemplateRoleMapper menuTemplateRoleMapper;

    @Override
    public MenuTemplateModel getByIdentifier(String identifier) {
        ParamCheck.notBlank(identifier, AuthBasei18nCode.TARGET_IDENTIFIER);
        var condition = new HumanCondition().filterEqual(MenuTemplateModel::getTemplateId, getUserTemplateId()).filterEqual("identifier", identifier);
        return getModelMapper().first(condition);
    }

    @Override
    public boolean exists(String id) {
        ParamCheck.notBlank(id, AuthBasei18nCode.TARGET_ID);
        HumanCondition condition = new HumanCondition().filterEqual(MenuTemplateModel::getTemplateId, getUserTemplateId()).filterEqual(MenuTemplateModel::getMenuId, id);
        var result = getModelMapper().first(condition);
        return Objects.nonNull(result);
    }

    @Override
    public List<MenuTemplateModel> listByParent(String parentId) {
        ParamCheck.notBlank(parentId, AuthBasei18nCode.TARGET_PARENT);
        HumanCondition condition = new HumanCondition().filterEqual(MenuTemplateModel::getTemplateId, getUserTemplateId()).filterEqual(MenuTemplateModel::getParent, parentId).filterEqual(MenuTemplateModel::getVisible, AuthConstant.TARGET_FIELD_VALUE_VISIBLE_1);
        List<MenuTemplateModel> targetModels = getModelMapper().list(condition);
        targetModels.forEach(t -> t.setId(t.getMenuId()));
        return targetModels;
    }

    @Override
    public List<TargetBo> getTree(String parentId, Integer type) {
        if (Objects.isNull(type)) {
            
            type = 1;
        }
        if (StringUtils.isBlank(parentId)) {
            
            List<MenuTemplateModel> targets = listAll(AuthConstant.DEFAULT_TENANT, type);
            if (CollectionUtil.isEmpty(targets)) {
                return new ArrayList<>();
            }
            List<TargetBo> tree = toTree(targets);
            sortTree(tree);
            return tree;
        } else {
            
            MenuTemplateModel parentTarget = getModelMapper().get(parentId);
            if (parentTarget == null) {
                log.info("target:{} not existed!", parentId);
                throw new MpRuntimeException(AuthErrorCode.TARGET_NOT_EXISTED.format(parentId));
            }
            List<MenuTemplateModel> targets = new LinkedList<>();
            List<MenuTemplateModel> queue = new LinkedList<>();
            queue.add(parentTarget);
            while (!queue.isEmpty()) {
                var header = queue.remove(0);
                if (header != null) {
                    var childModes = listByParent(header.getMenuId());
                    targets.addAll(childModes);
                }
                if (targets.size() > 2000) {
                    log.error("too many menus ,may be config wrong , targets is {}", JsonCommon.stringify(parentId));
                    throw new MpRuntimeException(BaseErrorCode.ERROR);
                }
            }
            List<TargetBo> tree = toTree(targets);
            sortTree(tree);
            return tree;
        }
    }

    @Override
    public List<MenuTemplateModel> listAll(String tenant, Integer type) {
        HumanCondition humanCondition = new HumanCondition();
        if (Objects.nonNull(type)) {
            humanCondition.filterEqual(MenuTemplateModel::getType, type);
        }
        if (StringUtils.isNotBlank(tenant)) {
            humanCondition.filterEqual(MenuTemplateModel::getTenant, tenant);
        }
        humanCondition.filterEqual(MenuTemplateModel::getTemplateId, getUserTemplateId()).filterEqual(MenuTemplateModel::getVisible, AuthConstant.TARGET_FIELD_VALUE_VISIBLE_1);
        List<MenuTemplateModel> targets = getModelMapper().list(humanCondition);
        return targets;
    }

    @Override
    public List<MenuTemplateModel> listAllIgnoreVisible(String tenant, Integer type) {
        HumanCondition humanCondition = new HumanCondition();
        if (Objects.nonNull(type)) {
            humanCondition.filterEqual(MenuTemplateModel::getType, type);
        }
        if (StringUtils.isNotBlank(tenant)) {
            humanCondition.filterEqual(MenuTemplateModel::getTenant, tenant);
        }
        List<MenuTemplateModel> targets = getModelMapper().list(humanCondition);
        return targets;
    }

    @Override
    public List<MenuTemplateModel> listByMultiTypes(String tenant, Integer[] types) {
        HumanCondition humanCondition = new HumanCondition();
        if (Objects.nonNull(types) && types.length > 0) {
            humanCondition.filterIn(MenuTemplateModel::getType, types);
        }
        if (StringUtils.isNotBlank(tenant)) {
            humanCondition.filterEqual(MenuTemplateModel::getTenant, tenant);
        }
        humanCondition.filterEqual(MenuTemplateModel::getTemplateId, getUserTemplateId()).filterEqual(MenuTemplateModel::getVisible, AuthConstant.TARGET_FIELD_VALUE_VISIBLE_1);
        List<MenuTemplateModel> targets = getModelMapper().list(humanCondition);
        return targets;
    }

    @Override
    public boolean deleteAll() {
        HumanCondition humanCondition = new HumanCondition().filterGreaterThan(MenuTemplateModel::getPk, AuthConstant.TARGET_FIELD_VALUE_PK_0);
        int result = getModelMapper().filterDelete(humanCondition);
        log.info("deleted {} rows", result);
        return true;
    }

    private List<TargetBo> toTree(List<MenuTemplateModel> targets) {
        ParamCheck.notEmpty(targets, AuthBasei18nCode.TARGET_LIST);
        Map<String, List<MenuTemplateModel>> map = new HashMap<>();
        Map<String, TargetBo> mapTmp = new HashMap<>();

        
        for (MenuTemplateModel targetm : targets) {
            TargetBo targetBo = new TargetBo();
            BeanUtils.copyProperties(targetm, targetBo);
            targetBo.setId(targetm.getMenuId());
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
            if (StringUtils.isBlank(node.getParent())) {
                parentTargets.add(node);
            }
        }

        return parentTargets;
    }

    
    private void sortTree(List<TargetBo> targets) {
        targets.forEach(t -> {
            if (CollectionUtil.isNotEmpty(t.getChildren())) {
                sortTree(t.getChildren());
            }
        });

        targets.sort(Comparator.comparingInt(BaseModel::getPk));
    }

    private MenuTemplateMapper getModelMapper() {
        return this.menuTemplateMapper;
    }

    private String getUserTemplateId() {
        String userMenuTemplateId = "1";
        if (UserUtils.getLoginUser() != null) {
            String userId = UserUtils.getLoginUser().getId();
            var roles = userRoleMapper.list(new HumanCondition().filterEqual(UserRoleModel::getUser, userId).orderBy(UserRoleModel::getPk));
            if (CollectionUtils.isNotEmpty(roles)) {
                String roleId = roles.get(0).getRole();
                var roleMenuTemplate = menuTemplateRoleMapper.first(new HumanCondition().filterEqual(MenuTemplateRoleModel::getRoleId, roleId));
                if (roleMenuTemplate != null) {
                    userMenuTemplateId = roleMenuTemplate.getTemplateId();
                }
            }
        }
        return userMenuTemplateId;
    }
}