package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 查看当前用户，是否有权对用户进行增删改查
 * @Author zhouxinghuai
 * @Date：2022/05/12 11:15
 */
@Slf4j
@Service
public class OperateUserPermissionService extends OperatePermissionService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;


    public List<String> getExcludeRoleIdsByLoginUserForSearchingUsers() {

        List<String> result = new ArrayList<>();
        result.addAll(roleService.getSystemUnVisibleRoleIds());
        boolean isSupserUser = isLoginUserSuperUser();
        if (isSupserUser) {
            // 超级用户不受限制
            return result;
        }

        boolean isAdmin = isLoginUserAdmin();
        if (isAdmin) {
            // 管理员可以查询同级管理员用户，但不能查询超级用户
            return result;
        }

        // 普通用户不可查询管理员和超级用户
        result.add(AuthConstant.ROLE_ADMIN_ID);
        return result;
    }

    public List<String> getExcludeUserIdsByLoginUserForSearchingUsers() {

        boolean isSupserUser = isLoginUserSuperUser();
        if (isSupserUser) {
            // 超级用户不受限制
            return Collections.emptyList();
        }

        // 其他角色用户不允许查询超级用户
        return Arrays.asList(AuthConstant.ROOT_USER_ID);
    }

    /**
     * targetUserId是否是登录用户自身
     * @param targetUserId
     * @return
     */
    public boolean isOperateSelf(List<String> targetUserIds) {
        UserModel currentLoginUser = UserUtils.getLoginUser();
        if (Objects.nonNull(currentLoginUser) && CollectionUtil.isNotEmpty(targetUserIds)) {
            String currentUserId = currentLoginUser.getId();
            return targetUserIds.contains(currentUserId);
        }

        return false;
    }


    /**
     * 根据操作用户id集合进行权限校验
     * 如：批量删除用户，批量更新用户等
     * @param targetUserIds
     */
    public void checkByTargetUserIdsForSearching(List<String> targetUserIds) {
        if (CollectionUtil.isEmpty(targetUserIds)) {
            return;
        }

        // 自己可以查自己
        // 去重
        List<String> distinctUIds = targetUserIds.stream().distinct().collect(Collectors.toList());
        if (distinctUIds.size() == 1) {
            UserModel currentLoginUser = UserUtils.getLoginUser();
            if (Objects.nonNull(currentLoginUser) && Objects.equals(currentLoginUser.getId(), targetUserIds.get(0))) {
                return;
            }
        }

        boolean isSupserUser = isLoginUserSuperUser();
        if (!isSupserUser) {
            if (targetUserIds.contains(AuthConstant.ROOT_USER_ID)) {
                log.error("Admin or normal role user cannot fetch root user info...");
                throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
            }
        }

        List<RoleModel> roleModels = userRoleService.listByUserIds(targetUserIds);
        List<String> targetRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());
        super.checkByTargetRoleIdsForSearching(targetRoleIds);
    }

    /**
     * 根据操作用户id集合进行权限校验
     * 如：批量删除用户，批量更新用户等
     * @param targetUserIds
     */
    public void checkByTargetUserIds(List<String> targetUserIds) {
        if (CollectionUtil.isEmpty(targetUserIds)) {
            return;
        }

        // 自己可以查自己
        // 去重
        List<String> distinctUIds = targetUserIds.stream().distinct().collect(Collectors.toList());
        if (distinctUIds.size() == 1) {
            UserModel currentLoginUser = UserUtils.getLoginUser();
            if (Objects.nonNull(currentLoginUser) && Objects.equals(currentLoginUser.getId(), targetUserIds.get(0))) {
                return;
            }
        }

        List<RoleModel> roleModels = userRoleService.listByUserIds(targetUserIds);
        List<String> targetRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());
        super.checkByTargetRoleIds(targetRoleIds);
    }

    /**
     * 根据操作对象的所有角色名称检查权限
     * 如：excel批量导入
     * @param targetRoleNames
     */
    public void checkByTargetRoleNames(List<String> targetRoleNames) {
        if (CollectionUtil.isEmpty(targetRoleNames)) {
            return;
        }
        List<RoleModel> roleModels = roleService.listByNames(targetRoleNames);
        List<String> targetRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());
        super.checkByTargetRoleIds(targetRoleIds);
    }

}
