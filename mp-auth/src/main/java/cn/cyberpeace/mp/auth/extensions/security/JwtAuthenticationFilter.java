package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.base.context.LoginUserContext;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.hutool.crypto.digest.MD5;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @Resource(name = "authCacheManager_6H")
    private CacheManager cacheManager;

    public static String SKIP_JWT_FILTER = "SKIP_JWT_FILTER";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Object skipJwtFilter = request.getAttribute(JwtAuthenticationFilter.SKIP_JWT_FILTER);
        boolean skip = Objects.nonNull(skipJwtFilter) && (Boolean) skipJwtFilter;
        if (!skip) {
            String token = JwtCommon.getToken(request);
            if (StringUtils.isNotBlank(token)) {
                UserModel user = JwtCommon.verifyToken(token);
                UsernamePasswordAuthenticationToken authenticationToken = null;
                if (Objects.nonNull(user)) {
                    // 第一步就要将请求类型塞入request attribute，否则后面的loadAuthorities()就可能出问题，参见浙能定制版commit id:f7cdf17f6c7e0bbc36e771a79db981a0ffd4990f
                    UserUtils.setLoginUser2RequestAttr(request, user.getId(), user.getUsername(), LogConstant.USER_TYPE_USER);

                    // 暂时不用加载权限信息
                    Set<GrantedAuthority> authoritySet = authUserDetailsService.loadAuthorities(user.getId());

                    authenticationToken = new UsernamePasswordAuthenticationToken(new AuthUserDetails(user, authoritySet), token,
                            authoritySet);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    LoginUserContext loginUserContext = new LoginUserContext();
                    loginUserContext.setValue(user.getId());

                    refreshToken(response, token, user);
                }
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }


    private void refreshToken(HttpServletResponse response, String token, UserModel userModel) {
        String md5 = MD5.create().digestHex(token);
        Cache tokenCache = cacheManager.getCache("_token");
        String jwt = tokenCache.get(md5, String.class);
        if (StringUtils.isBlank(jwt)) {
            Date date = JwtUtils.getTokenExpiredTime(token);
            if (Objects.isNull(date)) {
                return;
            }
            log.info("refreshToken, username:{}", userModel.getUsername());
            LocalDateTime expiredTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(expiredTime.minusHours(6)) && now.isBefore(expiredTime)) {
                log.warn("token is expired in less than 6 hours, auto refresh token...");
                jwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
            }
        }

        if (StringUtils.isNotBlank(jwt)) {
            // 后端种下一个新的token
            setCookie(response, "cr_jwttoken", jwt, "/", null, Integer.MAX_VALUE);
        }
    }


        private void setCookie(HttpServletResponse response, String name, String value, String path, String domain, int maxAge) {
            Cookie cookie = new Cookie(name, null);
            cookie.setPath(path);
            if (StringUtils.isNotBlank(domain)) {
                cookie.setDomain(domain);
            }
            cookie.setMaxAge(maxAge);
            // httponly = false
            cookie.setHttpOnly(false);
            try {
                cookie.setValue(URLEncoder.encode(value, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.addCookie(cookie);
        }

}
