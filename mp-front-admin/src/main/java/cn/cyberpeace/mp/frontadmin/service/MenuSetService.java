package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.auth.bo.target.TargetActivBo;
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
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.frontadmin.constant.AdminConstant;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationItem;
import cn.cyberpeace.mp.frontadmin.model.MenuExtraInfoModel;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import cn.cyberpeace.mp.frontadmin.service.sys.activmenu.MenuExtraInfoService;
import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MenuSetService {

    @Resource
    private MenuTemplateMapper menuTemplateMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private MenuTemplateRoleMapper menuTemplateRoleMapper;

    @Resource
    @Qualifier("targetServiceImpl")
    private TargetService targetService;

    @Resource
    private MenuExtraInfoService menuExtraInfoService;

    @Autowired
    private SysActivationService activationService;


    
    public Set<String> getSystemAllVisibleMenuIds() {
        var activationResponse = activationService.status();
        List<String> allPositiveKeys = this.getPositiveKeys(activationResponse);
        return getVisibleMenuIdsBy(allPositiveKeys);
    }


    
    public Set<String> getVisibleMenuIdsBy(List<String> positiveKeys) {
        var activationResponse = activationService.status();
        String edition = String.valueOf(activationResponse.getEdition());

        var targetModels = targetService.listAll(null, null);
        var menuExtraInfos = menuExtraInfoService.listAll();

        Set<String> visibleMenuIds = new HashSet<>();
        for (var mi : menuExtraInfos) {
            String menuId = mi.getMenu();
            Optional<TargetModel> targetModelOptional = targetModels.stream().filter(t -> Objects.equals(t.getId(), menuId)).findFirst();
            if (!targetModelOptional.isPresent()) {
                continue;
            }

            boolean editionMatch = checkEditionMatch(mi, edition);
            boolean itemMatch = checkItemMatch(mi, positiveKeys);
            
            if (editionMatch && itemMatch) {
                TargetModel targetModel = targetModelOptional.get();
                visibleMenuIds.add(targetModel.getIdentifier());
            }
        }

        return visibleMenuIds;
    }

    public List<TargetBo> getSystemTreeMenusBy(Set<String> visibleMenuIds) {
        var userMenus = listAll(AuthConstant.DEFAULT_TENANT, 1, "1");
        var userVisibleMenus = userMenus.stream().filter(m -> visibleMenuIds.contains(m.getIdentifier())).collect(Collectors.toList());
        List<TargetBo> tree = toTree(userVisibleMenus);
        sortTree(tree);
        return tree;
    }

    public List<TargetBo> getCurrentUserTreeMenusBy(Set<String> visibleMenuIds) {
        var user = UserUtils.getLoginUser();
        var userMenus = this.getUserAllMenus(user.getId());
        var userVisibleMenus = userMenus.stream().filter(m -> visibleMenuIds.contains(m.getIdentifier())).collect(Collectors.toList());
        List<TargetBo> tree = toTree(userVisibleMenus);
        sortTree(tree);
        return tree;
    }

    public MenuTemplate importDefaultTemplate(MultipartFile file) {
        var content = readJsonStrFromMultipartFile(file);
        MenuTemplate menuTemplate = JsonCommon.snakeParse(content, new TypeReference<MenuTemplate>() {
        });
        return this.refreshTemplate(menuTemplate.getMenuTree(), "1");
    }

    public MenuTemplate addTemplate(List<TargetBo> inputTreeTargets) {
        return save2Template(inputTreeTargets, TextUtils.uuid());
    }

    public MenuTemplate refreshTemplate(List<TargetBo> inputTreeTargets, String templateId) {
        menuTemplateMapper.filterDelete(new HumanCondition().filterEqual(MenuTemplateModel::getTemplateId, templateId));
        return save2Template(inputTreeTargets, templateId);
    }

    private MenuTemplate save2Template(List<TargetBo> inputTreeTargets, String templateId) {
        List<TargetBo> listTargets = new ArrayList<>();
        List<TargetBo> queue = new LinkedList<>();
        queue.addAll(inputTreeTargets);
        while (!queue.isEmpty()) {
            var headTargetBo = queue.remove(0);
            if (CollectionUtils.isNotEmpty(headTargetBo.getChildren())) {
                for (var child : headTargetBo.getChildren()) {
                    child.setParent(headTargetBo.getIdentifier());
                }
                queue.addAll(headTargetBo.getChildren());
            }
            listTargets.add(headTargetBo);
            if (listTargets.size() > 2000) {
                log.error("too many menus ,may be config wrong , targets is {}", JsonCommon.stringify(inputTreeTargets));
                throw new MpRuntimeException(BaseErrorCode.ERROR);
            }
        }
        MenuTemplate menuTemplate = new MenuTemplate();
        menuTemplate.setTemplateId(templateId);
        menuTemplate.setMenuTree(inputTreeTargets);
        List<MenuTemplateModel> models = listTargets.stream().map(t -> {
            MenuTemplateModel model = new MenuTemplateModel();
            model.setTemplateId(menuTemplate.templateId);
            model.setName(t.getName());
            model.setEnName(t.getEnName());
            model.setParent(t.getParent());
            model.setIdentifier(t.getIdentifier());
            model.setMenuId(t.getIdentifier());
            model.setPath(t.getPath());
            model.setType(t.getType());
            model.setVisible(t.getVisible());
            model.setTenant(t.getTenant());
            return model;
        }).collect(Collectors.toList());
        menuTemplateMapper.bulkCreate(models);
        return menuTemplate;
    }

    public void updateRoleMenuTemplate(String roleId, String templateId) {
        var existModel = menuTemplateRoleMapper.first(new HumanCondition().filterEqual(MenuTemplateRoleModel::getRoleId, roleId));
        if (existModel != null) {
            MenuTemplateRoleModel updateModel = new MenuTemplateRoleModel();
            updateModel.setId(existModel.getId());
            updateModel.setTemplateId(templateId);
            menuTemplateRoleMapper.update(updateModel);
        } else {
            MenuTemplateRoleModel createModel = new MenuTemplateRoleModel();
            createModel.setTemplateId(templateId);
            createModel.setRoleId(roleId);
            menuTemplateRoleMapper.create(createModel);
        }
    }

    private List<MenuTemplateModel> getUserAllMenus(String userId) {
        var roles = userRoleMapper.list(new HumanCondition().filterEqual(UserRoleModel::getUser, userId).orderBy(UserRoleModel::getPk));
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
        return targets;
    }

    private List<MenuTemplateModel> listAll(String tenant, Integer type, String templateId) {
        HumanCondition humanCondition = new HumanCondition();
        if (Objects.nonNull(type)) {
            humanCondition.filterEqual(MenuTemplateModel::getType, type);
        }
        if (StringUtils.isNotBlank(tenant)) {
            humanCondition.filterEqual(MenuTemplateModel::getTenant, tenant);
        }
        humanCondition.filterEqual(MenuTemplateModel::getTemplateId, templateId);
        humanCondition.filterEqual(MenuTemplateModel::getVisible, AuthConstant.TARGET_FIELD_VALUE_VISIBLE_1);
        List<MenuTemplateModel> targets = menuTemplateMapper.list(humanCondition);
        return targets;
    }

    private List<TargetBo> toTree(List<MenuTemplateModel> targets) {
        ParamCheck.notEmpty(targets, AuthBasei18nCode.TARGET_LIST);
        Map<String, List<TargetModel>> map = new HashMap<>();
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

    @Data
    public static class MenuTemplate {
        @ApiModelProperty(value = "模板id")
        private String templateId;
        @ApiModelProperty(value = "模板名称")
        private String name;
        @ApiModelProperty(value = "需要关联上的角色Id")
        private String roleId;
        @ApiModelProperty(value = "模板里的菜单结构")
        private List<TargetBo> menuTree;
    }

    @Data
    public static class RoleTemplate {
        @ApiModelProperty(value = "角色id")
        private String roleId;
        @ApiModelProperty(value = "模板id")
        private String templateId;
    }

    private List<String> getPositiveKeys(ActivationResponse activationResponse) {
        List<ActivationItem> items = activationResponse.getItems();
        List<String> positiveKeys = new ArrayList<>();
        positiveKeys.add(AdminConstant.MENU_NO_AUTH);
        items.stream().forEach(item -> {
            if (Objects.nonNull(item.getType()) && Objects.equals(item.getType(), 1)) {
                if (Objects.nonNull(item.getValue()) && Objects.equals("1", item.getValue())) {
                    positiveKeys.add(item.getKey());
                }
            }
        });
        return positiveKeys;
    }

    private boolean checkEditionMatch(MenuExtraInfoModel mi, String edition) {
        boolean editionMatch = false;
        if (StringUtils.isNotEmpty(mi.getEditions())) {
            List<String> editions = Arrays.asList(mi.getEditions().split(","));
            editionMatch = editions.contains(edition);
        }
        return editionMatch;
    }

    private boolean checkItemMatch(MenuExtraInfoModel mi, List<String> positiveKeys) {
        boolean itemMatch = false;
        if (StringUtils.isNotEmpty(mi.getActivKeys())) {
            List<String> activKeys = Arrays.asList(mi.getActivKeys().split(","));
            for (String activKey : activKeys) {
                
                if (Objects.equals(activKey.trim(), TargetActivBo.NO_AUTH)) {
                    itemMatch = true;
                    break;
                }
            }
            if (!itemMatch) {
                
                for (String activKey : activKeys) {
                    for (String positiveKey : positiveKeys) {
                        if (Objects.equals(activKey.trim(), positiveKey.trim())) {
                            itemMatch = true;
                            break;
                        }
                    }
                }
            }
        }
        return itemMatch;
    }

    private String readJsonStrFromMultipartFile(MultipartFile file) {
        String jsonStr = "";
        try (Reader reader = new InputStreamReader(file.getInputStream(), "utf-8")) {
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
