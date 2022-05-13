package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
     * 根据操作用户id集合进行权限校验
     * 如：批量删除用户，批量更新用户等
     * @param targetUserIds
     */
    public void checkByTargetUserIds(List<String> targetUserIds) {
        if (CollectionUtil.isEmpty(targetUserIds)) {
            return;
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
