 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClientAuthenticationFilter.java.
 // File size 2849.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.log.constant.LogConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

 /**
  * 客户端身份验证过滤器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
@Slf4j
public class ClientAuthenticationFilter extends OncePerRequestFilter {

     /**
      * 客户端id报头
      */
     public static final String CLIENT_ID_HEADER = "clientId";

     /**
      * 客户端秘密头
      */
     public static final String CLIENT_SECRET_HEADER = "clientSecret";

     /**
      * 客户端细节服务
      */
     @Autowired
    private ClientDetailsService clientDetailsService;

     /**
      * 用户服务
      */
     @Autowired
    private UserService userService;

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
        String clientId = request.getHeader(CLIENT_ID_HEADER);
        String clientSecret = request.getHeader(CLIENT_SECRET_HEADER);
        if(StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret)) {
            ClientDetailsModel clientDetails = clientDetailsService.getByClientId(clientId);
            if (Objects.nonNull(clientDetails) && Objects.equals(clientDetails.getType(), ClientDetailsModel.CLIENT_TYPE_UNAUTH)) {
                // 第一步就要将请求类型塞入request attribute，否则后面的loadAuthorities()就可能出问题，参见浙能定制版commit id:f7cdf17f6c7e0bbc36e771a79db981a0ffd4990f
                // 后台接口操作统一当做admin操作
                var result = userService.get(AuthConstant.ROOT_USER_ID);
                if (Objects.nonNull(result.getSource()) && Objects.nonNull(result.getSource().getObj())) {
                    UserModel rootUser = result.getSource().getObj();
                    UserUtils.setLoginUser2RequestAttr(request, rootUser.getId(), rootUser.getUsername(), LogConstant.USER_TYPE_USER);
                }
                request.setAttribute(JwtAuthenticationFilter.SKIP_JWT_FILTER, Boolean.TRUE);
            }
        }

        filterChain.doFilter(request, response);

    }

}
