package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/2 11:15
 */
@Slf4j
@Service
public class OperatorPermissionCheckService {

    @Autowired
    private UserService userService;

    public void check(List<String> targetRoleIds) {
        if (CollectionUtil.isEmpty(targetRoleIds)) {
            return;
        }

        UserModel currentUser = UserUtils.getLoginUser();
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
                // 管理员不可操作管理员账号，但是可以操作普通用户
                if (targetRoleIds.contains(AuthConstant.ADMIN_ROLE_ID)) {
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
