package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.repository.UserPermissionMapper;
import cn.cyberpeace.mp.auth.service.*;
import cn.cyberpeace.mp.auth.service.fields.UserPermissionFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserPermissionServiceImpl
        extends BaseModelServiceImpl<UserPermissionModel, UserPermissionMapper, UserPermissionFields, QueryParams>
        implements UserPermissionService {

    public static final String CACHE_VAL_USER_TARGETS= "USER_TARGETS_CACHE";


    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RolePermissionModelService rolePermissionService;

    @Autowired
    @Qualifier("targetServiceImpl")

    private TargetService targetService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrgPermissionService orgPermissionService;

    @Autowired
    public UserPermissionServiceImpl(UserPermissionMapper modelMapper, UserPermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Cacheable(cacheManager = AuthCacheConfig.CACHE_MANAGER_NORMAL, value = CACHE_VAL_USER_TARGETS)
    public List<TargetModel> listTargetsByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        Set<TargetModel> authorizedTargetsSet = new HashSet<>();

        
        List<RoleModel> roles = userRoleService.listByUserId(userId);

        
        boolean isAdmin = hasAdminRole(roles);
        if (isAdmin) {
            
            List<TargetModel> allTargets = targetService.listAll(AuthConstant.DEFAULT_TENANT, null);
            return allTargets;
        }

        
        List<String> roleIds = roles.stream().map(RoleModel::getId).collect(Collectors.toList());
        List<TargetModel> roleTargets = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(roleIds)) {
            roleTargets = rolePermissionService.listTargetsByRoleIds(roleIds);
        }
        authorizedTargetsSet.addAll(roleTargets);

        
        
        

        
        
        
        
        
        
        
        

        List<TargetModel> targetModelList = authorizedTargetsSet.stream().collect(Collectors.toList());
        return targetModelList;
    }

    @Override
    public List<UserModel> listUsersByTargetId(String targetId) {
        ParamCheck.notBlank(targetId, AuthBasei18nCode.TARGET_ID);

        List<UserModel> users = getUsersByTargetId(targetId);
        users.stream().forEach(userModel -> userService.setDefaultIfNoAvatar(userModel));
        return users;
    }

    @Override
    @Cacheable(cacheManager = AuthCacheConfig.CACHE_MANAGER_NORMAL, value = CACHE_VAL_USER_TARGETS)
    public List<TargetModel> listMenusByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        List<TargetModel> targets = listTargetsByUserId(userId);
        if (CollectionUtil.isNotEmpty(targets)) {
            return targets.stream().filter(t -> Objects.equals(t.getType(), TargetModel.TYPE_MENU)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    public List<TargetBo> getTreeByUserId(String userId) {
        List<TargetModel> targets = listTargetsByUserId(userId);
        if (CollectionUtil.isEmpty(targets)) {
            return new ArrayList<>();
        }
        List<TargetBo> tree = toTree(targets);
        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindUserTargets(String userId, List<String> targetIds, boolean cleanOldTargets) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);

        var result = userService.get(userId);
        ResultUtils.throwExceptionIfError(result, AuthErrorCode.ACCOUNT_NOT_EXISTED);

        if (cleanOldTargets) {
            
            boolean success = deleteByUserId(userId);
            if (!success) {
                throw new MpRuntimeException(AuthErrorCode.USER_TARGET_DELETE_ERROR);
            }
        }

        if (CollectionUtil.isEmpty(targetIds)) {
            
            return true;
        }

        
        targetIds.forEach(tid -> {
            
            UserPermissionModel userPerm = new UserPermissionModel();
            userPerm.setOwner(userId);
            userPerm.setTarget(tid);
            userPerm.setTenant(AuthConstant.DEFAULT_TENANT);
            var re = create(userPerm);
            ResultUtils.throwExceptionIfError(re, AuthErrorCode.USER_TARGET_ADD_ERROR);
        });

        return true;
    }


    public List<TargetBo> toTree(List<TargetModel> targets) {
        ParamCheck.notEmpty(targets, AuthBasei18nCode.TARGET_LIST);
        Map<String, List<TargetModel>> map = new HashMap<>();
        Map<String, TargetBo> mapTmp = new HashMap<>();

        
        for (TargetModel targetModel : targets) {
            TargetBo targetBo = new TargetBo();
            BeanUtils.copyProperties(targetModel, targetBo);
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

        List<TargetBo> parentMenus = new ArrayList<>();
        
        for (TargetBo node : mapTmp.values()) {
            if(StringUtils.isBlank(node.getParent())) {
                parentMenus.add(node);
            }
        }

        return parentMenus;
    }




    public boolean hasAdminRole(List<RoleModel> roles) {
        return roles.stream().anyMatch(r -> Objects.equals(r.getId(), AuthConstant.ROLE_ADMIN_ID));
    }


    public boolean  deleteByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(UserPermissionModel::getOwner, userId);
        getModelMapper().filterDelete(humanCondition);
        return true;
    }

    public boolean deleteByTargetId(String targetId) {
        ParamCheck.notBlank(targetId, AuthBasei18nCode.TARGET_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(UserPermissionModel::getTarget, targetId);
        getModelMapper().filterDelete(humanCondition);
        return true;
    }

    @Override
    public List<TargetModel> listMenusByLoginUser() {
        UserModel user = UserUtils.getLoginUser();
        if (Objects.isNull(user)) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_LOGIN);
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

    public List<UserModel> getUsersByTargetId(String targetId) {
        HumanCondition permissionCondition = new HumanCondition()
                .filterEqual(UserPermissionModel::getTarget, targetId)
                .select(UserPermissionModel::getOwner);
        List<UserPermissionModel> userPermissionModels = getModelMapper().list(permissionCondition);
        List<String> owners = userPermissionModels.stream()
                .map(UserPermissionModel::getOwner)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(owners)) {
            return new ArrayList<>();
        }
        HumanCondition userCondition = new HumanCondition()
                .filterIn(UserModel::getId, owners.toArray())
                .distinct();
        List<UserModel> userModels = userMapper.list(userCondition);
        return userModels;
    }
}
