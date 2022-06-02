package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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


    /**
     * targetUserId是否是登录用户自身
     * @param targetUserId
     * @return
     */
    public boolean isOperateSelf(String targetUserId) {
        UserModel currentLoginUser = UserUtils.getLoginUser();
        if (Objects.nonNull(currentLoginUser) && Objects.equals(currentLoginUser.getId(), targetUserId)) {
            return true;
        }

        return false;
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
