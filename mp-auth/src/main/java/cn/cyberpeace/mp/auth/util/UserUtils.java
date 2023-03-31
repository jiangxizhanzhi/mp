package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.constant.RoleAuthControlConstant;
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
import java.util.*;
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
     * @description: 通过当前用户id获取同组织下的所有用户id
     * @param userId
     * @return Set<String>
     */
    public static Set<String> getSameOrgUserIds(String userId){
        List<UserModel> sameOrgUsers = userService.getSameOrgUsers(userId);
        return sameOrgUsers.stream().map(UserModel::getId).collect(Collectors.toSet());
    }


    /**
     * @description: 获取当前用户所在组织下的所有用户id
     * @throws
     * @return Set<String>
     */
    public static Set<String> getSameOrgUserIdsByCurrentUser(){
        var user = getLoginUserDtoWithoutOrg();
        return Objects.isNull(user)?new HashSet<>():getSameOrgUserIds(user.getId());
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

    /**
     * @description: 判断当前用户是否为
     * @throws
     * @return boolean
     */
    public static boolean isCurrentUserTrailAccount(){
        var user = getLoginUserDtoWithoutOrg();
        return Objects.isNull(user) ? false
                : RoleAuthControlConstant.TRAIL_TYPE.TRUE.getType().equals(user.getIsTrialAccount());
    }

    /**
     * @description: 判断用户是否为试用角色
     * @param userId
     * @return boolean
     */
    public static boolean isUserTrailAccount(String userId){
        var userModel = getUser(userId);
        var user = userModel2UserDtoWithoutOrg(userModel);
        return Objects.isNull(user) ? false
                : RoleAuthControlConstant.TRAIL_TYPE.TRUE.getType().equals(user.getIsTrialAccount());
    }



    public static UserBo getLoginUserDtoWithoutOrg() {
        UserModel currentUserModel = getLoginUser();
        if (Objects.isNull(currentUserModel)) {
            return null;
        }
        return userService.userModel2UserDtoWithoutOrg(currentUserModel);
    }

    /**
     * @description: 用户模型数据补充为用户业务数据（不包含组织）
     * @param userModel
     * @return UserBo
     */
    public static UserBo userModel2UserDtoWithoutOrg(UserModel userModel){
        return userService.userModel2UserDtoWithoutOrg(userModel);
    }



}
