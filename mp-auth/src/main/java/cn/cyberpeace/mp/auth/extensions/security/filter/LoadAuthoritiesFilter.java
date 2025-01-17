 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LoadAuthoritiesFilter.java.
 // File size 4036.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.extensions.security.client.ClientAuthenticationToken;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.log.constant.LogConstant;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;
import java.util.Set;

 /**
  * 负载权威过滤器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
@Slf4j
public class LoadAuthoritiesFilter extends OncePerRequestFilter {

     /**
      * 身份验证用户细节服务
      */
     @Autowired
    private AuthUserDetailsService authUserDetailsService;

     /**
      * 客户端细节服务
      */
     @Autowired
    private ClientDetailsService clientDetailsService;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Object userId = request.getAttribute(LogConstant.USER_ID_KEY);
        Object userType = request.getAttribute(LogConstant.USER_TYPE_KEY);

        if (Objects.equals(userType, LogConstant.USER_TYPE_USER) && Objects.nonNull(userId)) {
            // 账号密码认证
            String uid = (String) userId;
            UserModel userModel = UserUtils.getUser(uid);
            if (Objects.nonNull(userModel)) {
                String token = JwtCommon.getToken(request);
                Set<GrantedAuthority> authoritySet = authUserDetailsService.loadAuthorities((String) userId);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(new AuthUserDetails(userModel, authoritySet), token, authoritySet);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        if (Objects.equals(userType, LogConstant.USER_TYPE_CLIENT)) {
            // 客户端认证
            String clientId = request.getHeader(ClientAuthenticationFilter.CLIENT_ID_HEADER);
            String clientSecret = request.getHeader(ClientAuthenticationFilter.CLIENT_SECRET_HEADER);
            ClientDetailsModel clientDetails = clientDetailsService.getByClientId(clientId);
            if (Objects.nonNull(clientDetails)) {
                // client认证, 赋有超级用户的所有角色，权限
                Set<GrantedAuthority> authoritySet = authUserDetailsService.loadAuthorities(AuthConstant.ROOT_USER_ID);
                var clientAuthenticationToken = new ClientAuthenticationToken(clientDetails, clientSecret, authoritySet);
                clientAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(clientAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
