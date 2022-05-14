package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.extensions.security.client.ClientAuthenticationToken;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
@Slf4j
public class ClientAuthenticationFilter extends OncePerRequestFilter {

    public static final String CLIENT_ID_HEADER = "clientId";

    public static final String CLIENT_SECRET_HEADER = "clientSecret";

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientId = request.getHeader(CLIENT_ID_HEADER);
        String clientSecret = request.getHeader(CLIENT_SECRET_HEADER);
        if(StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret)) {
            ClientDetailsModel clientDetails = clientDetailsService.getByClientId(clientId);
            log.info("client auth... clientId:{}, clientSecret:{}, clientDetails:{}", clientId, clientSecret, clientDetails);
            if (clientDetails != null && Objects.equals(clientDetails.getType(), ClientDetailsModel.CLIENT_TYPE_UNAUTH)) {
                // 第一步就要将请求类型塞入request attribute，否则后面的loadAuthorities()就可能出问题，参见浙能定制版commit id:f7cdf17f6c7e0bbc36e771a79db981a0ffd4990f
                UserUtils.setLoginUser2RequestAttr(request, clientDetails.getId(), clientDetails.getClientId(), LogConstant.USER_TYPE_CLIENT);
                // 认证
                var clientAuthenticationToken = new ClientAuthenticationToken(clientDetails, clientSecret, null);
                clientAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(clientAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);

    }

}
