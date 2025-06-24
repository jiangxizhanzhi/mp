 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlACFilter.java.
 // File size 9951.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.filter;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OperatePermissionService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontbusiness.config.FrontBusinessConfig;
import cn.cyberpeace.mp.frontbusiness.constant.FrontBizErrorCode;
import cn.cyberpeace.mp.frontbusiness.service.UrlACWrapperService;
import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

 /**
  * url acfilter
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
public class UrlACFilter extends OncePerRequestFilter {

     /**
      *
      */
     private static final String NO_AUTH = "no_auth";

     /**
      * 匹配器
      */
     private static final PathMatcher MATCHER = new AntPathMatcher();

     /**
      * urlacwrapper服务
      */
     private UrlACWrapperService urlACWrapperService;

     /**
      * 人事处烫服务
      */
     private OperatePermissionService opPermService;
     /**
      * 前面业务配置
      */
     private FrontBusinessConfig frontBusinessConfig;

     /**
      * 映射器
      */
     private ObjectMapper mapper;

     /**
      * url acfilter
      *
      * @param urlACWrapperService urlacwrapper服务
      * @param opPermService       人事处烫服务
      * @param frontBusinessConfig 前面业务配置
      * @param mapper              映射器
      */
     public UrlACFilter(UrlACWrapperService urlACWrapperService, OperatePermissionService opPermService, FrontBusinessConfig frontBusinessConfig, ObjectMapper mapper) {
        this.urlACWrapperService = urlACWrapperService;
        this.opPermService = opPermService;
        this.frontBusinessConfig = frontBusinessConfig;
        this.mapper = mapper;
    }

     /**
      * 做内部过滤
      *
      * @param request     请求
      * @param response    响应
      * @param filterChain 过滤器链
      * @throws ServletException servlet异常
      * @throws IOException      ioexception
      */
     @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean skip = false;
        // 拦截器总开关是否开启
        if (Objects.equals(Boolean.FALSE, frontBusinessConfig.getUrlAcFilterEnabled())) {
            skip = true;
        }

        if (!skip) {
            // 是否是管理员或者超级用户
            if (opPermService.isLoginUserSuperUser() || opPermService.isLoginUserAdmin()) {
                skip = true;
            }
        }

        ServiceResult<Object> result = null;
        if (!skip) {
            String requestUrl = request.getRequestURI();
            if (StringUtils.isNotBlank(requestUrl)) {
                // 去除url中最后一个'/'
                if (requestUrl.length() == requestUrl.lastIndexOf('/') + 1) {
                    requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
                }
            }

            // 方法拦截是否开启
            //if (Objects.nonNull(urlACFilterConfig.getMethodMatch()) && urlACFilterConfig.getMethodMatch()) {
            //    String method = request.getMethod().toLowerCase();
            //    boolean isMethodMatch = isMethodMatch(requestUrl, method);
            //    if (!isMethodMatch) {
            //        // 请求方法不匹配
            //        log.warn("###URLACFILTER -> referer:{}, url:{}, method:{} not match!", request.getHeaders("referer"), requestUrl, method);
            //    }
            //}

            // 权限拦截是否开启
            if (Objects.nonNull(frontBusinessConfig.getUrlAcFilterAuthorityMatch()) && frontBusinessConfig.getUrlAcFilterAuthorityMatch()) {

                boolean isAuthoritiesMatchFlag = false;
                // 判断是否是excludeUrls
                List<String> excludeUrls = frontBusinessConfig.getUrlAcFilterExcludeUrls();
                if (CollectionUtil.isNotEmpty(excludeUrls)) {
                    for (String urlPattern : excludeUrls) {
                        if (MATCHER.match(urlPattern, requestUrl)) {
                            isAuthoritiesMatchFlag = true;
                            break;
                        }
                    }
                }

                if (!isAuthoritiesMatchFlag) {
                    isAuthoritiesMatchFlag = isAuthoritiesMatch(requestUrl);
                }

                if (!isAuthoritiesMatchFlag) {
                    // 权限不匹配
                    log.error("###URLACFILTER -> referer:{}, url:{} access denied!", request.getHeaders("referer"), requestUrl);
                    result = new ServiceResult<>(null, FrontBizErrorCode.URL_AC_AUTHORITY_NOT_MATCH.format(requestUrl));
                }
            }

        }

        if (Objects.nonNull(result)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            mapper.writeValue(writer, result.asResult());
            writer.flush();
            return;
        }

        filterChain.doFilter(request, response);
    }

     /**
      * 方法匹配
      *
      * @param requestUrl 请求url
      * @param method     方法
      * @return boolean
      */
     private boolean isMethodMatch(String requestUrl, String method) {
        Map<String, List<String>> urlMethodsMap = urlACWrapperService.getUrlMethodsMap();
        Map<String, List<String>> urlPatternMethodsMap = urlACWrapperService.getUrlPatternMethodsMap();


        boolean isAccurateUrlMethodMatch = isAccurateUrlMethodMatch(requestUrl, method, urlMethodsMap);

        if (isAccurateUrlMethodMatch) {
            return true;
        }

        boolean isPatternUrlMethodMatch = isPatternUrlMethodMatch(requestUrl, method, urlPatternMethodsMap);
        if(isPatternUrlMethodMatch) {
            return true;
        }
        return false;
    }

     /**
      * 当局匹配吗？
      *
      * @param requestUrl 请求url
      * @return boolean
      */
     private boolean isAuthoritiesMatch(String requestUrl) {
        Set<String> currentUserAuthorities = UserUtils.getLoginUserAuthorities();
        UserModel currentUser = UserUtils.getLoginUser();
        String currentUsername = Objects.nonNull(currentUser) ? currentUser.getUsername() : "";
        Map<String, List<String>> urlTargetsMap = urlACWrapperService.getUrlTargetsMap();
        Map<String, List<String>> urlPatternTargetsMap = urlACWrapperService.getUrlPatternTargetsMap();

        boolean isAccurateUrlAuthoritiesMatch = isAccurateUrlAuthoritiesMatch(requestUrl, urlTargetsMap, currentUserAuthorities);
        if (isAccurateUrlAuthoritiesMatch) {
            return true;
        }

        boolean isPatternUrlAuthoritiesMatch = isPatternUrlAuthoritiesMatch(requestUrl, urlPatternTargetsMap, currentUserAuthorities);
        if(isPatternUrlAuthoritiesMatch) {
            return true;
        }
        return false;
    }

     /**
      * 精确url-method匹配
      *
      * @param requestUrl
      * @param method        方法
      * @param urlMethodsMap
      * @return
      */
     private boolean isAccurateUrlMethodMatch(String requestUrl, String method, Map<String, List<String>> urlMethodsMap) {
        List<String> methods = urlMethodsMap.get(requestUrl);
        if (CollectionUtil.isNotEmpty(methods)) {
            return methods.contains(method);
        }
        return false;
    }

     /**
      * 规则url-method匹配
      *
      * @param requestUrl
      * @param method               方法
      * @param patternUrlMethodsMap
      * @return
      */
     private boolean isPatternUrlMethodMatch(String requestUrl, String method, Map<String, List<String>> patternUrlMethodsMap) {
        Set<String> urlPatterns = patternUrlMethodsMap.keySet();
        for (String urlPattern : urlPatterns) {
            if (MATCHER.match(urlPattern, requestUrl)) {
                List<String> methods = patternUrlMethodsMap.get(urlPattern);
                if (CollectionUtil.isNotEmpty(methods)) {
                    boolean methodMatch = methods.contains(method);
                    if (methodMatch) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

     /**
      * 精确url-authority匹配
      *
      * @param requestUrl
      * @param urlTargetsMap
      * @param currentUserAuthorities
      * @return
      */
     private boolean isAccurateUrlAuthoritiesMatch(String requestUrl, Map<String, List<String>> urlTargetsMap, Set<String> currentUserAuthorities) {

        List<String> urlTargetIds = urlTargetsMap.get(requestUrl);
        return isUserAuthoritiesMatch(currentUserAuthorities, urlTargetIds);
    }

     /**
      * 规则url-authority匹配
      *
      * @param requestUrl
      * @param urlTargetsMap
      * @param currentUserAuthorities
      * @return
      */
     private boolean isPatternUrlAuthoritiesMatch(String requestUrl, Map<String, List<String>> urlTargetsMap, Set<String> currentUserAuthorities) {
        Set<String> urlPatterns = urlTargetsMap.keySet();
        for (String urlPattern : urlPatterns) {
            if (MATCHER.match(urlPattern, requestUrl)) {
                List<String> urlTargetIds = urlTargetsMap.get(urlPattern);
                if (isUserAuthoritiesMatch(currentUserAuthorities, urlTargetIds)) {
                    return true;
                }
            }
        }
        return false;
    }

     /**
      * 用户的权限是否匹配url的权限需求定义
      *
      * @param currentUserAuthorities
      * @param urlTargetIds
      * @return
      */
     private boolean isUserAuthoritiesMatch(Set<String> currentUserAuthorities, List<String> urlTargetIds) {
        if (CollectionUtil.isNotEmpty(urlTargetIds)) {
            if (urlTargetIds.contains(NO_AUTH)) {
                return true;
            }
            for (String tid : urlTargetIds) {
                if (currentUserAuthorities.contains(tid)) {
                    return true;
                }
            }
        }
        return false;
    }


}
