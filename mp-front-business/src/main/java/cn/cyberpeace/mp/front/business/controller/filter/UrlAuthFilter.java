package cn.cyberpeace.mp.front.business.controller.filter;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OperatePermissionService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.front.business.dto.UrlAccessCtlDto;
import cn.cyberpeace.mp.front.business.service.UrlACService;
import cn.cyberpeace.mp.front.business.service.UrlACWrapperService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

/**
 * @Description: url拦截器
 * @Author zhouxinghuai
 * @Date：2022/5/19 16:43
 */
@Slf4j
public class UrlAuthFilter extends OncePerRequestFilter {

    private UrlACWrapperService urlACWrapperService;

    private OperatePermissionService opPermService;

    private Boolean enableUrlAcFilter;

    public UrlAuthFilter(UrlACWrapperService urlACWrapperService, OperatePermissionService opPermService, Boolean enableUrlAcFilter) {
        this.urlACWrapperService = urlACWrapperService;
        this.opPermService = opPermService;
        this.enableUrlAcFilter = enableUrlAcFilter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("UrlAuthFilter...");
        boolean skip = false;
        if (Objects.nonNull(enableUrlAcFilter) && !enableUrlAcFilter) {
            skip = true;
        }

        if(!skip) {
            if (opPermService.isLoginUserSuperUser() || opPermService.isLoginUserAdmin()) {
                skip = true;
            }
        }

        if (!skip) {

            //List<String> fuzzyUrls = urlacMap.keySet().stream().filter(url -> url.contains("*")).collect(Collectors.toList());
            //Set<String> currentUserAuthorities = UserUtils.getLoginUserAuthorities();
            //
            String url = request.getRequestURI();
            String method = request.getMethod().toLowerCase();
            //
            //List<UrlAccessCtlDto> urlAccessCtlDtos = urlacMap.get(url);
            //if (CollectionUtil.isNotEmpty(urlAccessCtlDtos)) {
            //
            //}

            //urlme

        }

        filterChain.doFilter(request, response);

    }



    private boolean isRequestMethodSupport(String url, String method) {
        Map<String, List<String>> urlMethodsMap = urlACWrapperService.getUrlMethodsMap();
        List<String> methods = urlMethodsMap.get(url);
        return methods.contains(method);
    }


    private boolean isUrlAuthorized(String url) {
        Set<String> userAuthorities = UserUtils.getLoginUserAuthorities();
        UserModel currentUser = UserUtils.getLoginUser();
        String currentUsername = Objects.nonNull(currentUser) ? currentUser.getUsername() : "";

        Map<String, List<String>> urlTargetsMap = urlACWrapperService.getUrlTargetsMap();
        List<String> urlTargetIds = urlTargetsMap.get(url);
        if (CollectionUtil.isNotEmpty(urlTargetIds)) {
            for (String userAuthority : userAuthorities) {
                for (String tid : urlTargetIds) {
                    if (Objects.equals(userAuthority, tid)) {
                        return true;
                    }
                }
            }
            log.warn("url:{} is blocked to current user:{} for not meetting specified url-authoritys:{}", url, currentUsername, urlTargetIds);
        }

        return false;
    }






}
