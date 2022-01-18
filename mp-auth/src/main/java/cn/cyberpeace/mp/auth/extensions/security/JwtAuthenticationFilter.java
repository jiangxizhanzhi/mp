package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = JwtCommon.getToken(request);
        if (StringUtils.isNotBlank(token)) {
            UserModel user = JwtCommon.verifyToken(token);
            UsernamePasswordAuthenticationToken authenticationToken = null;
            if (user != null) {
                // 暂时不用加载权限信息
                 Set<GrantedAuthority> authoritySet = authUserDetailsService.loadAuthorities(user.getId());

                authenticationToken = new UsernamePasswordAuthenticationToken(new AuthUserDetails(user, authoritySet), token,
                        authoritySet);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                UserUtils.setLoginUser2RequestAttr(request, user.getId(), user.getName(), LogConstant.USER_TYPE_USER);
            }

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

}
