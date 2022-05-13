package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 查看当前用户，是否有权对角色进行增删改查
 * @Author zhouxinghuai
 * @Date：2022/05/12 11:15
 */
@Slf4j
@Service
public class OperateRolePermissionCheckService extends OperatePermissionCheckService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;


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
