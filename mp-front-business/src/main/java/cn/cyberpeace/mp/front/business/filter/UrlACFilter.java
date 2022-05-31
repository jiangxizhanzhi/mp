package cn.cyberpeace.mp.front.business.filter;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OperatePermissionService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.front.business.config.UrlACFilterConfig;
import cn.cyberpeace.mp.front.business.constant.FrontBizErrorCode;
import cn.cyberpeace.mp.front.business.service.UrlACWrapperService;
import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
 * @Description: url拦截器
 * @Author zhouxinghuai
 * @Date：2022/5/19 16:43
 */
@Slf4j
public class UrlACFilter extends OncePerRequestFilter {

    private static final String NO_AUTH = "no_auth";

    private static final PathMatcher MATCHER = new AntPathMatcher();

    private UrlACWrapperService urlACWrapperService;

    private OperatePermissionService opPermService;

    private UrlACFilterConfig urlACFilterConfig;

    private ObjectMapper mapper;

    public UrlACFilter(UrlACWrapperService urlACWrapperService, OperatePermissionService opPermService, UrlACFilterConfig urlACFilterConfig, ObjectMapper mapper) {
        this.urlACWrapperService = urlACWrapperService;
        this.opPermService = opPermService;
        this.urlACFilterConfig = urlACFilterConfig;
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean skip = false;
        // 拦截器总开关是否开启
        if (Objects.nonNull(urlACFilterConfig) && !urlACFilterConfig.getEnabled()) {
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
                if (requestUrl.length() == requestUrl.lastIndexOf("/") + 1) {
                    requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
                }
            }

            String method = request.getMethod().toLowerCase();

            // 方法拦截是否开启
            if (Objects.nonNull(urlACFilterConfig.getMethodMatch()) && urlACFilterConfig.getMethodMatch()) {
                boolean isMethodMatch = isMethodMatch(requestUrl, method);
                if (!isMethodMatch) {
                    // 请求方法不匹配
                    log.warn("###URLACFILTER -> referer:{}, url:{}, method:{} not match!", request.getHeaders("referer"), requestUrl, method);
                    //throw new MpRuntimeException(FrontBizErrorCode.URL_AC_REQUEST_METHOD_NOT_MATCH.format(requestUrl, method));
                    //result = new ServiceResult<>(null, FrontBizErrorCode.URL_AC_REQUEST_METHOD_NOT_MATCH.format(requestUrl, method));
                }
            }

            // 权限拦截是否开启
            if (Objects.nonNull(urlACFilterConfig.getAuthorityMatch()) && urlACFilterConfig.getAuthorityMatch()) {
                boolean isAuthoritiesMatch = isAuthoritiesMatch(requestUrl);

                if (!isAuthoritiesMatch) {
                    // 权限不匹配
                    log.error("###URLACFILTER -> referer:{}, url:{} access denied!", request.getHeaders("referer"), requestUrl);
                    //throw new MpRuntimeException(FrontBizErrorCode.URL_AC_AUTHORITY_NOT_MATCH.format(requestUrl));
                    result = new ServiceResult<>(null, FrontBizErrorCode.URL_AC_AUTHORITY_NOT_MATCH.format(requestUrl, method));
                }
            }

        }

        if (Objects.nonNull(result)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            mapper.writeValue(writer, result.asResult());
            writer.flush();
            return;
        }

        filterChain.doFilter(request, response);
    }


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

        log.warn("isAccurateUrlMethodMatch:{}, isPatternUrlMethodMatch:{}", isAccurateUrlMethodMatch, isPatternUrlMethodMatch);
        log.warn("url:{} is blocked for method:{} not match!", requestUrl, method);

        return false;
    }


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

        log.warn("isAccurateUrlAuthoritiesMatch:{}, isPatternUrlAuthoritiesMatch:{}", isAccurateUrlAuthoritiesMatch, isPatternUrlAuthoritiesMatch);
        log.warn("url:{} is blocked to current user:{} for not has specified url-authoritys:{}", requestUrl, currentUsername, urlTargetsMap.get(requestUrl));

        return false;
    }

    /**
     * 精确url-method匹配
     *
     * @param requestUrl
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
