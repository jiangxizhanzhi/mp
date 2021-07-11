package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.log.constant.LogConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;


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
        ObjResult<UserModel> result = userService.get(id).getSource();
        user = result != null ? result.getObj() : null;
        return user;
    }


    public static void setLoginUser2RequestAttr(HttpServletRequest request, String id, String username, String userType) {
        if (Objects.isNull(request)) {
            request = ServletUtils.getRequest();
        }
        if (Objects.isNull(request)) {
            return;
        }
        request.setAttribute(LogConstant.USER_ID_KEY, id);
        request.setAttribute(LogConstant.USER_NAME_KEY, username);
        request.setAttribute(LogConstant.USER_TYPE_KEY, userType);
    }

    
    public static String getLoginUserType() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();

        if (Objects.nonNull(request)) {
            Object userType = request.getAttribute(LogConstant.USER_TYPE_KEY);
            if (Objects.nonNull(userType)) {
                return String.valueOf(userType);
            }
        }

        return null;
    }

    
    @Nullable
    public static UserModel getLoginUser() {
        UserModel user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUserDetails) {
            user = ((AuthUserDetails) principal).getUser();
        }

        if (principal instanceof ClientDetailsModel) {
            user = null;
        }

        return user;
    }


    public static Set<String> getLoginUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return Collections.emptySet();
        }

        Collection authorities = authentication.getAuthorities();
        Set<String> authoritySet = AuthorityUtils.authorityListToSet(authorities);
        return authoritySet;
    }



    public static UserBo getLoginUserDtoWithoutOrg() {
        UserModel currentUserModel = getLoginUser();
        if (Objects.isNull(currentUserModel)) {
            return null;
        }
        return userService.userModel2UserDtoWithoutOrg(currentUserModel);
    }



}
