package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
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

    /**
     * 获取当前登录用户类型
     * @return
     */
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

    /**
     * 获取当前登录用户
     * 如果返回null，有可能是client认证，也有可能是免认证的接口
     * 需要另外根据 request.getAttribute(LogConstant.USER_TYPE_KEY) 来判断
     * @return
     */
    @Nullable
    public static UserModel getLoginUser() {
        UserModel user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return null;
        }
        log.info("authentication type:{}", authentication.getClass().getSimpleName());
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUserDetails) {
            user = ((AuthUserDetails) principal).getUser();
        }

        if (principal instanceof ClientDetailsModel) {
            log.warn("Login account type is Client, return null!");
            user = null;
        }

        return user;
    }


    public static Set<String> getLoginUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return Collections.emptySet();
        }

        Set<GrantedAuthority> authoritySet = (Set<GrantedAuthority>) authentication.getAuthorities();
        if (CollectionUtil.isNotEmpty(authoritySet)) {
            Set<String> set = authoritySet.stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
            return set;
        }
        return Collections.emptySet();
    }



    public static UserDto getLoginUserDtoWithoutOrg() {
        UserModel currentUserModel = getLoginUser();
        if (Objects.isNull(currentUserModel)) {
            return null;
        }
        return userService.userModel2UserDtoWithoutOrg(currentUserModel);
    }



}
