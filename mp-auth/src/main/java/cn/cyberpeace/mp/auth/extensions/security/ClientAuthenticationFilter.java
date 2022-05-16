package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.extensions.security.client.ClientAuthenticationToken;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class ClientAuthenticationFilter extends OncePerRequestFilter {

    public static final String CLIENT_ID_HEADER = "clientId";

    public static final String CLIENT_SECRET_HEADER = "clientSecret";

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private AuthUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientId = request.getHeader(CLIENT_ID_HEADER);
        String clientSecret = request.getHeader(CLIENT_SECRET_HEADER);
        if(StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret)) {
            ClientDetailsModel clientDetails = clientDetailsService.getByClientId(clientId);
            log.info("client auth... clientId:{}", clientId);
            if (clientDetails != null && Objects.equals(clientDetails.getType(), ClientDetailsModel.CLIENT_TYPE_UNAUTH)) {
                // 第一步就要将请求类型塞入request attribute，否则后面的loadAuthorities()就会出问题
                UserUtils.setLoginUser2RequestAttr(request, clientDetails.getId(), clientDetails.getClientId(), LogConstant.USER_TYPE_CLIENT);

                // client认证, 赋有所有角色，资源权限
                Set<GrantedAuthority> authoritySet = userDetailsService.loadAuthorities(AuthConstant.ROOT_USER_ID);

                var clientAuthenticationToken = new ClientAuthenticationToken(clientDetails, clientSecret, authoritySet);
                clientAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(clientAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);

    }

}
