 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlAcInterceptor.java.
 // File size 9128.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.interceptor;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontbusiness.config.FrontBusinessConfig;
import cn.cyberpeace.mp.frontbusiness.constant.FrontBizErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @ClassName UrlAcInterceptor
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HandlerInterceptor
 */
@Slf4j
public class UrlAcInterceptor implements HandlerInterceptor {

    private final static Map<String, Boolean> EXCLUDE_URL_CACHE = new ConcurrentHashMap<>(8);

    private static final PathMatcher MATCHER = new AntPathMatcher();
    private final FrontBusinessConfig frontBusinessConfig;
    private final OperateUserPermissionService operateUserPermissionService;
    private final UserPermissionService userPermissionService;


    /**
     * UrlAcInterceptor
     *
     * @param frontBusinessConfig
     * @param operateUserPermissionService
     * @param userPermissionService
     */
    public UrlAcInterceptor(FrontBusinessConfig frontBusinessConfig, OperateUserPermissionService operateUserPermissionService, UserPermissionService userPermissionService) {
        this.frontBusinessConfig = frontBusinessConfig;
        this.operateUserPermissionService = operateUserPermissionService;
        this.userPermissionService = userPermissionService;
    }

    /**
     * preHandle
     *
     * @param request
     * @param response
     * @param handler
     * @return boolean
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 拦截器总开关是否开启
        if (Boolean.FALSE.equals(frontBusinessConfig.getUrlAcFilterEnabled())) {
            return true;
        } else if (operateUserPermissionService.isLoginUserSuperUser() || operateUserPermissionService.isLoginUserAdmin()) {
            return true;
        } else if (Boolean.TRUE.equals(frontBusinessConfig.getUrlAcFilterAuthorityMatch())) {
            if (urlIsExcludeAuthFlagFromCache(request)) {
                return true;
            } else {
                if (validateAuthTargetByHandlerMethod(request, handler)) {
                    return true;
                } else {
                    setAuthFailedResponse(request, response);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * validateAuthTargetByHandlerMethod
     *
     * @param request
     * @param handler
     * @return boolean
     */
    private boolean validateAuthTargetByHandlerMethod(HttpServletRequest request, Object handler) {
        if (handler instanceof HandlerMethod) {
            if (!((HandlerMethod) handler).getBeanType().getPackageName().startsWith("cn.cyberpeace")) {
                return true;
            }
            AuthTarget authTargetOnClass = ((HandlerMethod) handler).getBeanType().getAnnotation(AuthTarget.class);
            AuthTarget authTargetOnMethod = ((HandlerMethod) handler).getMethod().getAnnotation(AuthTarget.class);
            Set<String> neededOneOfTheTargets = new HashSet<>();

            //方法上面定义的target, 会覆盖类上面定义的target
            if (Objects.nonNull(authTargetOnMethod)) {
                neededOneOfTheTargets.addAll(getTargetsFromAnnotation(authTargetOnMethod));
            } else if (Objects.nonNull(authTargetOnClass)) {
                neededOneOfTheTargets.addAll(getTargetsFromAnnotation(authTargetOnClass));
            }


            if (CollectionUtils.isEmpty(neededOneOfTheTargets)) {
                String requestUrl = request.getRequestURI();
                requestUrl = trimLastSlash(requestUrl);
                log.error("url {} , method {}#{} not config auth info", requestUrl, ((HandlerMethod) handler).getBeanType()
                        .getName(), ((HandlerMethod) handler).getMethod().getName());
                throw new MpRuntimeException(AuthErrorCode.INTERFACE_NOT_CONFIG_AUTH_INFO);
                //包含no_auth, 该接口不需要权限
            } else if (neededOneOfTheTargets.contains(TargetEnum.NO_AUTH.getTargets()[0])){
                return true;
            } else {
                Set<String> existTargets = new HashSet<>(userPermissionService.listMenuIdentsByLoginUser());
                String commonTarget = neededOneOfTheTargets.stream().filter(existTargets::contains)
                        .findAny().orElse("");
                if (StringUtils.isBlank(commonTarget)) {
                    log.info("existTargets = {}", existTargets);
                    log.info("needTargets = {}", neededOneOfTheTargets);
                }
                return StringUtils.isNotBlank(commonTarget);
            }

        }

        return true;
    }

    /**
     * setAuthFailedResponse
     *
     * @param request
     * @param response
     */
    private void setAuthFailedResponse(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            String body = JsonCommon.snakeStringify(
                    new ServiceResult<>(null, FrontBizErrorCode.URL_AC_AUTHORITY_NOT_MATCH.format(request.getRequestURI()))
                            .asResult());
            writer.print(body);
            writer.flush();
        } catch (IOException e) {
            log.error("url auth fail, and encounter error while write response", e);
            throw new MpRuntimeException(e.getMessage());
        }
    }

    /**
     * getTargetsFromAnnotation
     *
     * @param authTarget
     * @return
     */
    private Set<String> getTargetsFromAnnotation(AuthTarget authTarget) {
        if (Objects.isNull(authTarget)) {
            return Collections.emptySet();
        } else {
            return Arrays.stream(authTarget.value()).filter(Objects::nonNull)
                    .flatMap(targetEnum -> Arrays.stream(targetEnum.getTargets())).collect(Collectors.toSet());
        }
    }

    /**
     * urlIsExcludeAuthFlagFromCache
     *
     * @param request
     * @return boolean
     */
    private boolean urlIsExcludeAuthFlagFromCache(HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        requestUrl = trimLastSlash(requestUrl);

        if (Objects.nonNull(EXCLUDE_URL_CACHE.get(requestUrl))) {
            return EXCLUDE_URL_CACHE.get(requestUrl);
        } else {
            boolean flagByCompute = urlIsExcludeAuthFlag(requestUrl);
            updateExcludeUrlCache(requestUrl, flagByCompute);
            return flagByCompute;
        }
    }

    /**
     * urlIsExcludeAuthFlag
     *
     * @param requestUrl
     * @return boolean
     */
    private boolean urlIsExcludeAuthFlag(String requestUrl) {
        List<String> excludeUrls = frontBusinessConfig.getUrlAcFilterExcludeUrls();
        if (CollectionUtils.isNotEmpty(excludeUrls)) {
            for (String urlPattern : excludeUrls) {
                if (MATCHER.match(urlPattern, requestUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * updateExcludeUrlCache
     *
     * @param requestUrl
     * @param flag
     */
    private void updateExcludeUrlCache(String requestUrl, boolean flag) {
        EXCLUDE_URL_CACHE.put(requestUrl, flag);
    }

    /**
     * trimLastSlash
     *
     * @param requestUrl
     * @return
     */
    private String trimLastSlash(String requestUrl) {
        if (StringUtils.isNotBlank(requestUrl)) {
            if (requestUrl.endsWith("/")) {
                requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
            }
        }
        return requestUrl;
    }
}
