package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.authbase.context.LoginUserContext;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.constant.LogConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;


@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource(name = "authCacheManager_6H")
    private CacheManager cacheManager;

    private final ObjectMapper mapper;

    @Autowired
    public JwtAuthenticationFilter(MappingJackson2HttpMessageConverter messageConverter) {
        this.mapper = messageConverter.getObjectMapper();
    }

    public static final String SKIP_JWT_FILTER = "SKIP_JWT_FILTER";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Object skipJwtFilter = request.getAttribute(JwtAuthenticationFilter.SKIP_JWT_FILTER);
        boolean skip = Objects.nonNull(skipJwtFilter) && (Boolean) skipJwtFilter;
        if (!skip) {

                String token = JwtCommon.getToken(request);
                if (StringUtils.isNotBlank(token)) {
                    UserModel user;
                    try {
                        user = JwtCommon.verifyToken(token);
                    } catch (MpRuntimeException e) {
                        log.info(e.getMessage());
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        ServiceResult<Object> result = ServiceResult.builder().serviceCode(e.getServiceCode()).build().asResult();
                        PrintWriter writer = response.getWriter();
                        mapper.writeValue(writer, result);
                        writer.flush();
                        return;
                    }
                    if (Objects.nonNull(user)) {
                        
                        UserUtils.setLoginUser2RequestAttr(request, user.getId(), user.getUsername(), LogConstant.USER_TYPE_USER);

                        LoginUserContext loginUserContext = new LoginUserContext();
                        loginUserContext.setValue(user.getId());
                        refreshToken(response, token, user);
                    }
                }

        }

        filterChain.doFilter(request, response);
    }


    private void refreshToken(HttpServletResponse response, String token, UserModel userModel) {
        Cache tokenCache = cacheManager.getCache("_token");
        String newJwt = tokenCache.get(token, String.class);
        if (StringUtils.isBlank(newJwt)) {
            Date date = JwtUtils.getTokenExpiredTime(token);
            if (Objects.isNull(date)) {
                return;
            }
            LocalDateTime expiredTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(expiredTime.minusHours(6)) && now.isBefore(expiredTime)) {
                log.warn("token is expired in less than 6 hours, auto refresh token...");
                newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
                tokenCache.put(token, newJwt);
            }
        }

        if (StringUtils.isNotBlank(newJwt)) {
            
            setCookie(response, "cr_jwttoken", newJwt, "/", null, Integer.MAX_VALUE);

            response.setHeader(AuthConstant.JWT_HEADER_KEY, newJwt);
            response.setHeader(AuthConstant.CP_TOKEN_REFRESHED_HEADER_KEY, "true");
        }
    }


    private void setCookie(HttpServletResponse response, String name, String value, String path, String domain, int maxAge) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath(path);
        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }
        cookie.setMaxAge(maxAge);
        
        cookie.setHttpOnly(false);
        try {
            cookie.setValue(URLEncoder.encode(value, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.addCookie(cookie);
    }

}
