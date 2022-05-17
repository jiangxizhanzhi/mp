package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.target.TargetDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.auth.repository.UserPermissionMapper;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.RolePermissionService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserPermissionFields;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserPermissionServiceImpl
        extends BaseModelServiceImpl<UserPermissionModel, UserPermissionMapper, UserPermissionFields, QueryParams>
        implements UserPermissionService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private TargetService targetService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private OrgPermissionService orgPermissionService;

    @Autowired
    private OperateUserPermissionService opUserPermService;

    @Autowired
    public UserPermissionServiceImpl(UserPermissionMapper modelMapper, UserPermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Cacheable(value = "targets_cache", key = "'list_by_user_id_'+#userId")
    public List<TargetModel> listByUserId(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        Set<TargetModel> authorizedTargetsSet = new HashSet<>();

        // 用户绑定的所有角色绑定的target
        List<RoleModel> roles = userRoleService.listByUserId(userId);

        // 是否是管理员
        boolean isAdmin = hasAdminRole(roles);
        if(isAdmin) {
            // 直接查询该租户下所有权限返回
            List<TargetModel> allTargets = targetService.listAll(AuthConstant.DEFAULT_TENANT, null);

            return allTargets;
        }

        // 角色绑定的target
        roles.forEach((r) -> {
            List<TargetModel> roleTargets = rolePermissionService.listTargetsByRoleId(r.getId());
            authorizedTargetsSet.addAll(roleTargets);
        });

        // 用户直接绑定的target
        List<TargetModel> userTargets = getModelMapper().listByUserId(userId);
        authorizedTargetsSet.addAll(userTargets);

        // 用户所在组织绑定的target
        List<OrgModel> userOrgs = orgUserService.listOrgsByUserId(userId);
        Set<TargetModel> allTargetsByOrgSet = new HashSet<>();
        userOrgs.forEach(o -> {
            Set<TargetModel> tset = orgPermissionService.listTargetsByOrgIdIncludeAllParentsOrg(o.getId());
            allTargetsByOrgSet.addAll(tset);
        });
        authorizedTargetsSet.addAll(allTargetsByOrgSet);


        List<TargetModel> targetModelList = authorizedTargetsSet.stream().collect(Collectors.toList());
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


    @Cacheable(value = "targets_cache", key = "'tree_by_user_id_'+#userId")
    public List<TargetDto> getTreeByUserId(String userId) {
        List<TargetModel> targets = listByUserId(userId);
        if (CollectionUtil.isEmpty(targets)) {
            return new ArrayList<>();
        }
        List<TargetDto> tree = toTree(targets);
        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "targets_cache", allEntries = true)
    public boolean bindUserTargets(String userId, List<String> targetIds, boolean cleanOldTargets) {
        Assert.notNull(userId, "userId不能为空");
        opUserPermService.checkByTargetUserIds(Arrays.asList(userId));
        var result = userService.get(userId);
        ResultUtils.throwExceptionIfError(result, AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");

        if (cleanOldTargets) {
            // 清除用户原来的权限
            boolean success = deleteByUserId(userId);
            if (!success) {
                throw new MpRuntimeException("清除用户权限失败！");
            }
        }

        if (CollectionUtil.isEmpty(targetIds)) {
            // 完成，直接返回
            return true;
        }

        // 添加用户角色
        targetIds.forEach(tid -> {
            // todo 校验权限是否存在
            UserPermissionModel userPerm = new UserPermissionModel();
            userPerm.setOwner(userId);
            userPerm.setTarget(tid);
            userPerm.setTenant(AuthConstant.DEFAULT_TENANT);
            var re = create(userPerm);
            ResultUtils.throwExceptionIfError(re, "创建用户-权限关系失败！");
        });

        return true;
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
        return roles.stream().anyMatch(r -> Objects.equals(r.getIdentifier(), AuthConstant.ROLE_ADMIN));
    }


    public boolean  deleteByUserId(String userId) {
        Assert.notNull(userId, "userid不能为空");

//        var objResult = userService.get(userId).getSource();
//        if (objResult == null || objResult.getObj() == null) {
//            new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");
//        }
        getModelMapper().deleteByUserId(userId);
        return true;
    }

    public boolean deleteByTargetId(String targetId) {
        Assert.notNull(targetId, "targetId不能为空");
        getModelMapper().deleteByTargetId(targetId);
        return true;
    }

    @Override
    public List<TargetModel> listMenusByLoginUser() {
        UserModel user = UserUtils.getLoginUser();
        if (Objects.isNull(user)) {
            log.info("user not login!");
            return Collections.emptyList();
        }
        List<TargetModel> menus = listMenusByUserId(user.getId());
        return menus;
    }


    @Override
    public List<String> listMenuIdentsByLoginUser() {
        List<TargetModel> menus = listMenusByLoginUser();
        List<String> menuIdents = menus.stream().map(m -> m.getIdentifier()).collect(Collectors.toList());
        return menuIdents;
    }
}
