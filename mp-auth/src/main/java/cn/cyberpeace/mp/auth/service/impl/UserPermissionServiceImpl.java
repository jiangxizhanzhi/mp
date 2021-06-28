package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.TargetDto;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.auth.repository.UserPermissionMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.fields.UserPermissionFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserPermissionServiceImpl
        extends BaseModelServiceImpl<UserPermissionModel, UserPermissionMapper, UserPermissionFields>
        implements UserPermissionService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private TargetService targetService;

    @Autowired
    public UserPermissionServiceImpl(UserPermissionMapper modelMapper, UserPermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public List<TargetModel> listByUserId(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        Set<TargetModel> authorizedTargets = new HashSet<>();


        // 用户绑定的所有角色绑定的target
        List<RoleModel> roles = userRoleService.listByUserId(userId);

        // 是否是管理员
        boolean isAdmin = hasAdminRole(roles);
        if(isAdmin) {
            // 直接查询该租户下所有权限返回
            List<TargetModel> targets = targetService.listByTenantId(AuthConstant.DEFAULT_TENANT_ID);
            return targets;
        }

        roles.forEach((r) -> {
            List<TargetModel> targets = rolePermissionService.listTargetsByRoleId(r.getId());
            authorizedTargets.addAll(targets);
        });

        // 用户直接绑定的target
        List<TargetModel> targets1 = getModelMapper().listByUserId(userId);
        authorizedTargets.addAll(targets1);

        // todo 用户所在组织绑定的角色绑定的target

        // todo 用户所在组织直接绑定的target

        List<TargetModel> targetModelList = authorizedTargets.stream().collect(Collectors.toList());
        return targetModelList;
    }

    @Override
    public List<TargetModel> listMenusByUserId(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        List<TargetModel> targets = listByUserId(userId);
        if (CollectionUtil.isNotEmpty(targets)) {
            return targets.stream().filter(t -> Objects.equals(t.getType(), TargetModel.TYPE_MENU)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    public List<TargetDto> getTreeByUserId(String userId) {
        List<TargetModel> targets = listByUserId(userId);
        if (CollectionUtil.isEmpty(targets)) {
            return new ArrayList<>();
        }
        List<TargetDto> tree = toTree(targets);
        return tree;
    }



    public List<TargetDto> toTree(List<TargetModel> targets) {
        Assert.notEmpty(targets, "menus should not be empty!");
        Map<String, List<TargetModel>> map = new HashMap<>();
        Map<String, TargetDto> mapTmp = new HashMap<>();

        //Save all nodes to a map
        for (TargetModel targetModel : targets) {
            TargetDto targetDto = new TargetDto();
            BeanUtils.copyProperties(targetModel, targetDto);
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
//                    mapTmp.put(entry.getId(), current);
                }
            }

        }

        List<TargetDto> parentMenus = new ArrayList<>();
        //get the root
        for (TargetDto node : mapTmp.values()) {
            if(StringUtils.isBlank(node.getParent())) {
                parentMenus.add(node);
            }
        }

        return parentMenus;
    }




    public boolean hasAdminRole(List<RoleModel> roles) {
        return roles.stream().anyMatch(r -> Objects.equals(r.getIdentifier(), AuthConstant.ADMIN));
    }

}
