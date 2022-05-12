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
public class OperateRolePermissionCheckService {

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
        checkByTargetRoleIds(targetRoleIds);
    }

    /**
     * 根据操作对象的所有角色id集合检查权限
     * @param targetRoleIds
     */
    public void checkByTargetRoleIds(List<String> targetRoleIds) {
        if (CollectionUtil.isEmpty(targetRoleIds)) {
            return;
        }

        UserModel currentUser = UserUtils.getLoginUser();
        // 如果是客户端认证的，直接放行
        if (Objects.nonNull(currentUser)) {
            UserDto userDto = userService.userModel2UserDtoWithoutOrg(currentUser);
            log.info("current user:{}", userDto);
            boolean isSupserUser = Objects.equals(userDto.getIsSuperUser(), 1) ? true : false;
            boolean isAdmin = Objects.equals(userDto.getIsAdmin(), 1) ? true : false;

            // 超级用户不限制
            if (isSupserUser) {
                return;
            }

            // 自身是非超级用户的管理员
            if (isAdmin) {
                // 管理员不可操作拥有管理员，超级用户角色的账号，但是可以操作普通用户
                // 管理员甚至无权修改自己的信息
                if (targetRoleIds.contains(AuthConstant.ADMIN_ROLE_ID) || targetRoleIds.contains(AuthConstant.SUPERUSER_ROLE_ID)) {
                    log.error("permission denied for target role is admin");
                    throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
                }
                return;
            }

            // 普通用户不允许操作
            log.error("permission denied for normal user!");
            throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
        }

    }

}
