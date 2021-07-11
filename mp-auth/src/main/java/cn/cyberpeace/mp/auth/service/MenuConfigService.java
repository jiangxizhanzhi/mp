package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.MenuTemplateModel;
import cn.cyberpeace.mp.auth.model.MenuTemplateRoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.MenuTemplateMapper;
import cn.cyberpeace.mp.auth.repository.MenuTemplateRoleMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class MenuConfigService {

    @Resource
    private MenuTemplateMapper modelMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private MenuTemplateRoleMapper menuTemplateRoleMapper;

    public List<TargetBo> getUserTreeMenuBy(String userId, List<String> authorityMenuIds) {
        String userMenuTemplateId = "1";
        var roles = userRoleMapper.list(new HumanCondition().filterEqual(UserRoleModel::getUser, userId).orderBy(UserRoleModel::getPk));
        if (CollectionUtils.isNotEmpty(roles)) {
            String roleId = roles.get(0).getRole();
            var roleMenuTemplate = menuTemplateRoleMapper.first(new HumanCondition().filterEqual(MenuTemplateRoleModel::getRoleId, roleId));
            if (roleMenuTemplate != null) {
                userMenuTemplateId = roleMenuTemplate.getTemplateId();
            }
        }
        List<MenuTemplateModel> targets = listAll(AuthConstant.DEFAULT_TENANT, 1, userMenuTemplateId);
        Set<String> authorityMenuIdsSet = new HashSet<>(authorityMenuIds);
        targets = targets.stream().filter(t -> authorityMenuIdsSet.contains(t.getIdentifier())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(targets)) {
            return new ArrayList<>();
        }
        List<TargetBo> tree = toTree(targets);
        sortTree(tree);
        return tree;
    }

    public List<MenuTemplateModel> listAll(String tenant, Integer type, String templateId) {
        HumanCondition humanCondition = new HumanCondition();
        if (Objects.nonNull(type)) {
            humanCondition.filterEqual(MenuTemplateModel::getType, type);
        }
        if (StringUtils.isNotBlank(tenant)) {
            humanCondition.filterEqual(MenuTemplateModel::getTenant, tenant);
        }
        humanCondition.filterEqual(MenuTemplateModel::getTemplateId, templateId);
        humanCondition.filterEqual(MenuTemplateModel::getVisible, AuthConstant.TARGET_FIELD_VALUE_VISIBLE_1);
        List<MenuTemplateModel> targets = modelMapper.list(humanCondition);
        return targets;
    }

    public List<TargetBo> getTree() {
        var user = UserUtils.getLoginUser();
        var roles = userRoleMapper.list(new HumanCondition().filterEqual(UserRoleModel::getUser, user.getId()).orderBy(UserRoleModel::getPk));
        List<MenuTemplateModel> targets = null;
        if (CollectionUtils.isEmpty(roles)) {
            targets = listAll(AuthConstant.DEFAULT_TENANT, 1, "1");
        } else {
            String roleId = roles.get(0).getRole();
            String templateId = menuTemplateRoleMapper.first(new HumanCondition().filterEqual(MenuTemplateRoleModel::getRoleId, roleId)).getTemplateId();
            targets = listAll(AuthConstant.DEFAULT_TENANT, 1, templateId);
        }
        if (CollectionUtil.isEmpty(targets)) {
            return new ArrayList<>();
        }
        List<TargetBo> tree = toTree(targets);
        sortTree(tree);
        return tree;
    }

    private List<TargetBo> toTree(List<MenuTemplateModel> targets) {
        ParamCheck.notEmpty(targets, AuthBasei18nCode.TARGET_LIST);
        Map<String, List<TargetModel>> map = new HashMap<>();
        Map<String, TargetBo> mapTmp = new HashMap<>();

        
        Locale locale = LocaleContextHolder.getLocale();
        for (MenuTemplateModel targetm : targets) {
            TargetBo targetBo = new TargetBo();
            BeanUtils.copyProperties(targetm, targetBo);
            if (Objects.equals(Locale.US, locale)) {
                targetBo.setName(targetm.getEnName());
            }
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
}