package cn.cyberpeace.mp.front.business.filter;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OperatePermissionService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.front.business.config.UrlACFilterConfig;
import cn.cyberpeace.mp.front.business.constant.FrontBizErrorCode;
import cn.cyberpeace.mp.front.business.service.UrlACWrapperService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @Description: url拦截器
 * @Author zhouxinghuai
 * @Date：2022/5/19 16:43
 */
@Slf4j
public class UrlACFilter extends OncePerRequestFilter {

    private static final PathMatcher MATCHER = new AntPathMatcher();

    private UrlACWrapperService urlACWrapperService;

    private OperatePermissionService opPermService;

    private UrlACFilterConfig urlACFilterConfig;

    public UrlACFilter(UrlACWrapperService urlACWrapperService, OperatePermissionService opPermService, UrlACFilterConfig urlACFilterConfig) {
        this.urlACWrapperService = urlACWrapperService;
        this.opPermService = opPermService;
        this.urlACFilterConfig = urlACFilterConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("UrlAuthFilter...");
        boolean skip = false;
        // 拦截器总开关是否开启
        if (Objects.nonNull(urlACFilterConfig) && !urlACFilterConfig.getEnabled()) {
            skip = true;
        }

        if (!skip) {
            if (opPermService.isLoginUserSuperUser() || opPermService.isLoginUserAdmin()) {
                skip = true;
            }
        }

        if (!skip) {
            String url = request.getRequestURI();
            String method = request.getMethod().toLowerCase();

            // 方法拦截是否开启
            if (Objects.nonNull(urlACFilterConfig.getMethodMatch()) && urlACFilterConfig.getMethodMatch()) {
                boolean isMethodMatch = isMethodMatch(url, method);
                if (!isMethodMatch) {
                    // 请求方法不匹配
                    log.error("url:{} method:{} not match!", url, method);
                    throw new MpRuntimeException(FrontBizErrorCode.URL_AC_REQUEST_METHOD_NOT_MATCH.format(url, method));
                }
            }

            // 权限拦截是否开启
            if (Objects.nonNull(urlACFilterConfig.getAuthorityMatch()) && urlACFilterConfig.getAuthorityMatch()) {
                boolean isAuthoritiesMatch = isAuthoritiesMatch(url);

                if (!isAuthoritiesMatch) {
                    // 权限不匹配
                    log.error("url:{} access denied!", url);
                    throw new MpRuntimeException(FrontBizErrorCode.URL_AC_AUTHORITY_NOT_MATCH.format(url));
                }
            }

        }

        filterChain.doFilter(request, response);

    }


    private boolean isMethodMatch(String url, String method) {
        Map<String, List<String>> urlMethodsMap = urlACWrapperService.getUrlMethodsMap();

        boolean isAccurateUrlMethodMatch = isAccurateUrlMethodMatch(url, method, urlMethodsMap);

        if (isAccurateUrlMethodMatch) {
            return true;
        }

        boolean isPatternUrlMethodMatch = isPatternUrlMethodMatch(url, method, urlMethodsMap);
        if(isPatternUrlMethodMatch) {
            return true;
        }

        log.warn("isAccurateUrlMethodMatch:{}, isPatternUrlMethodMatch:{}", isAccurateUrlMethodMatch, isPatternUrlMethodMatch);
        log.warn("url:{} is blocked for method:{} not match!", url, method);

        return false;
    }


    private boolean isAuthoritiesMatch(String url) {
        Set<String> currentUserAuthorities = UserUtils.getLoginUserAuthorities();
        UserModel currentUser = UserUtils.getLoginUser();
        String currentUsername = Objects.nonNull(currentUser) ? currentUser.getUsername() : "";
        Map<String, List<String>> urlTargetsMap = urlACWrapperService.getUrlTargetsMap();

        boolean isAccurateUrlAuthoritiesMatch = isAccurateUrlAuthoritiesMatch(url, urlTargetsMap, currentUserAuthorities);
        if (isAccurateUrlAuthoritiesMatch) {
            return true;
        }

        boolean isPatternUrlAuthoritiesMatch = isPatternUrlAuthoritiesMatch(url, urlTargetsMap, currentUserAuthorities);
        if(isPatternUrlAuthoritiesMatch) {
            return true;
        }

        log.warn("isAccurateUrlAuthoritiesMatch:{}, isPatternUrlAuthoritiesMatch:{}", isAccurateUrlAuthoritiesMatch, isPatternUrlAuthoritiesMatch);
        log.warn("url:{} is blocked to current user:{} for not has specified url-authoritys:{}", url, currentUsername, urlTargetsMap.get(url));

        return false;
    }

    /**
     * 精确url-method匹配
     *
     * @param url
     * @return
     */
    private boolean isAccurateUrlMethodMatch(String url, String method, Map<String, List<String>> urlMethodsMap) {
        List<String> methods = urlMethodsMap.get(url);
        if (CollectionUtil.isNotEmpty(methods)) {
            return methods.contains(method);
        }
        return false;
    }


    /**
     * 规则url-method匹配
     *
     * @param url
     * @return
     */
    private boolean isPatternUrlMethodMatch(String url, String method, Map<String, List<String>> urlMethodsMap) {
        Set<String> urlPatterns = urlMethodsMap.keySet();
        for (String urlPattern : urlPatterns) {
            if (MATCHER.match(urlPattern, url)) {
                List<String> methods = urlMethodsMap.get(url);
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
     * @param url
     * @return
     */
    private boolean isAccurateUrlAuthoritiesMatch(String url, Map<String, List<String>> urlTargetsMap, Set<String> currentUserAuthorities) {

        List<String> urlTargetIds = urlTargetsMap.get(url);
        return isUserAuthoritiesMatch(currentUserAuthorities, urlTargetIds);
    }

    /**
     * 规则url-authority匹配
     *
     * @param url
     * @return
     */
    private boolean isPatternUrlAuthoritiesMatch(String url, Map<String, List<String>> urlTargetsMap, Set<String> currentUserAuthorities) {
        Set<String> urlPatterns = urlTargetsMap.keySet();
        for (String urlPattern : urlPatterns) {
            if (MATCHER.match(urlPattern, url)) {
                List<String> urlTargetIds = urlTargetsMap.get(url);
                if (isUserAuthoritiesMatch(currentUserAuthorities, urlTargetIds)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 用户的权限是否匹配url的权限需求定义
     * @param currentUserAuthorities
     * @param urlTargetIds
     * @return
     */
    private boolean isUserAuthoritiesMatch(Set<String> currentUserAuthorities, List<String> urlTargetIds) {
        if (CollectionUtil.isNotEmpty(urlTargetIds)) {
            for (String userAuthority : currentUserAuthorities) {
                for (String tid : urlTargetIds) {
                    if (Objects.equals(userAuthority, tid)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
