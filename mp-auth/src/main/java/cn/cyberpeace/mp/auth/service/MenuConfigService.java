package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.MenuTemplateModel;
import cn.cyberpeace.mp.auth.model.MenuTemplateRoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.MenuTemplateMapper;
import cn.cyberpeace.mp.auth.repository.MenuTemplateRoleMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class MenuConfigService {

    @Resource
    private MenuTemplateMapper modelMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private MenuTemplateRoleMapper menuTemplateRoleMapper;

    /**
     * getUserTreeMenuBy
     *
     * @param userId
     * @param authorityMenuIds
     * @return
     */
    public List<TargetBo> getUserTreeMenuBy(String userId, List<String> authorityMenuIds) {
        String userMenuTemplateId = "1";
        List<UserRoleModel> roles = userRoleMapper.list(new HumanCondition().filterEqual(UserRoleModel::getUser, userId).orderBy(UserRoleModel::getPk));
        if (CollectionUtils.isNotEmpty(roles)) {
            String roleId = roles.get(0).getRole();
            MenuTemplateRoleModel roleMenuTemplate = menuTemplateRoleMapper.first(new HumanCondition().filterEqual(MenuTemplateRoleModel::getRoleId, roleId));
            if (roleMenuTemplate != null) {
                userMenuTemplateId = roleMenuTemplate.getTemplateId();
            }
        }
        List<MenuTemplateModel> targets = listAll(AuthConstant.DEFAULT_TENANT, 1, userMenuTemplateId);
        Set<String> authorityMenuIdsSet = new HashSet<>(authorityMenuIds);
        List<MenuTemplateModel> list = new ArrayList<>();
        for (MenuTemplateModel t : targets) {
            if (authorityMenuIdsSet.contains(t.getIdentifier())) {
                list.add(t);
            }
        }
        targets = list;
        if (CollectionUtils.isEmpty(targets)) {
            return new ArrayList<>();
        }
        List<TargetBo> tree = toTree(targets);

        // 将后台的菜单添加返回到前台中
        List<TargetBo> backChildren = null;
        for (TargetBo node : tree){
            if ("后台".equals(node.getName())){
                backChildren = node.getChildren();
            }
            if ("前台".equals(node.getName()) && null != backChildren){
                node.getChildren().addAll(backChildren);
                backChildren = null;
            }
        }
        if (null != backChildren){
            for (TargetBo node : tree) {
                if ("前台".equals(node.getName())){
                    node.getChildren().addAll(backChildren);
                }
            }
        }

        // 排序
        sortTree(tree);
        return tree;
    }

    /**
     * listAll
     *
     * @param tenant
     * @param type
     * @param templateId
     * @return
     */
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

    /**
     * getTree
     *
     * @return
     */
    public List<TargetBo> getTree() {
        UserModel user = UserUtils.getLoginUser();
        List<UserRoleModel> roles = userRoleMapper.list(new HumanCondition().filterEqual(UserRoleModel::getUser, user.getId()).orderBy(UserRoleModel::getPk));
        List<MenuTemplateModel> targets = null;
        if (CollectionUtils.isEmpty(roles)) {
            targets = listAll(AuthConstant.DEFAULT_TENANT, 1, "1");
        } else {
            String roleId = roles.get(0).getRole();
            String templateId = menuTemplateRoleMapper.first(new HumanCondition().filterEqual(MenuTemplateRoleModel::getRoleId, roleId)).getTemplateId();
            targets = listAll(AuthConstant.DEFAULT_TENANT, 1, templateId);
        }
        if (CollectionUtils.isEmpty(targets)) {
            return new ArrayList<>();
        }
        List<TargetBo> tree = toTree(targets);
        sortTree(tree);
        return tree;
    }

    /**
     * toTree
     *
     * @param targets
     * @return
     */
    private List<TargetBo> toTree(List<MenuTemplateModel> targets) {
        ParamCheck.notEmpty(targets, AuthBasei18nCode.TARGET_LIST);
        Map<String, TargetBo> mapTmp = new HashMap<>();

        //Save all nodes to a map
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

        //loop and assign parent/child relationships
        for (Map.Entry<String, TargetBo> entry : mapTmp.entrySet()) {
            TargetBo current = entry.getValue();
            String parentId = current.getParent();

            if (StringUtils.isNotBlank(parentId)) {
                TargetBo parent = mapTmp.get(parentId);
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

        List<TargetBo> parentTargets = new ArrayList<>();
        //get the root
        for (TargetBo node : mapTmp.values()) {
            if (StringUtils.isBlank(node.getParent())) {
                parentTargets.add(node);
            }
        }

        return parentTargets;
    }

    /**
     * sortTree
     *
     * @param targets
     */
    private void sortTree(List<TargetBo> targets) {
        for (TargetBo t : targets) {
            if (CollectionUtils.isNotEmpty(t.getChildren())) {
                sortTree(t.getChildren());
            }
        }
        targets.sort(Comparator.comparingInt(BaseModel::getPk));
    }
}