package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/13 11:57
 */
@Slf4j
@Service
public class OperatePermissionCheckService {

    /**
     * 根据操作对象的所有角色id集合检查权限
     * @param targetRoleIds
     */
    public void checkByTargetRoleIds(List<String> targetRoleIds) {
        if (CollectionUtil.isEmpty(targetRoleIds)) {
            return;
        }

        UserDto currentUserDto = UserUtils.getLoginUserDtoWithoutOrg();
        // 如果是客户端认证的，直接放行
        if (Objects.nonNull(currentUserDto)) {
            boolean isSupserUser = Objects.equals(currentUserDto.getIsSuperUser(), 1) ? true : false;
            boolean isAdmin = Objects.equals(currentUserDto.getIsAdmin(), 1) ? true : false;

            // 超级用户不限制
            if (isSupserUser) {
                return;
            }

            // 自身是非超级用户的管理员
            if (isAdmin) {
                // 管理员不可操作拥有管理员，超级用户角色的账号，但是可以操作普通用户
                // 管理员甚至无权修改自己的信息
                if (targetRoleIds.contains(AuthConstant.ADMIN_ROLE_ID) || targetRoleIds.contains(AuthConstant.SUPERUSER_ROLE_ID)) {
                    log.error("permission denied! Cause target role is admin or superuser! current username:{}, isAdmin:{}, isSuperUser:{}", currentUserDto.getUsername(), isAdmin, isSupserUser);
                    throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
                }
                return;
            }

            // 普通用户不允许操作
            log.error("permission denied for normal user! username:{}, isAdmin:{}, isSuperUser:{}", currentUserDto.getUsername(), isAdmin, isSupserUser);
            throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
        }

    }

}
