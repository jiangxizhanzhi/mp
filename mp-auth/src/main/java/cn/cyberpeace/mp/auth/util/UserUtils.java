package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Component
@Slf4j
public class UserUtils {
    private static UserService userService;

    @SuppressWarnings({"java:S3010"})
    private UserUtils(UserService userService) {
        UserUtils.userService = userService;
    }

    public static UserModel getUser(String id) {
        UserModel user;
        // TODO 缓存获取
        ObjResult<UserModel> result = userService.get(id).getSource();
        user = result != null ? result.getObj() : null;
        return user;
    }


    public static void setLoginUser2RequestAttr(HttpServletRequest request, String id, String name, String userType) {
        if (Objects.isNull(request)) {
            request = ServletUtils.getRequest();
        }
        if (Objects.isNull(request)) {
            return;
        }
        request.setAttribute(LogConstant.USER_ID_KEY, id);
        request.setAttribute(LogConstant.USER_NAME_KEY, name);
        request.setAttribute(LogConstant.USER_TYPE_KEY, userType);
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public static UserModel getLoginUser() {
        UserModel user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return null;
        }
        log.info("authentication name:{}  authentication type:{}", authentication.getName(), authentication.getClass());
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUserDetails) {
            user = ((AuthUserDetails) principal).getUser();
        }

        if (principal instanceof ClientDetailsModel) {
            log.error("Login account type is Client, ID:{}, return null!", ((ClientDetailsModel) principal).getClientId());
//            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_TYPE_ERROR);
            user = null;
        }

        return user;
    }


    public static UserDto getLoginUserDtoWithoutOrg() {
        UserModel currentUserModel = getLoginUser();
        return userService.userModel2UserDtoWithoutOrg(currentUserModel);
    }


}
