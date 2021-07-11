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


@Component
@Slf4j
public class ClientAuthenticationFilter extends OncePerRequestFilter {

    public static final String CLIENT_ID_HEADER = "clientId";

    public static final String CLIENT_SECRET_HEADER = "clientSecret";

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientId = request.getHeader(CLIENT_ID_HEADER);
        String clientSecret = request.getHeader(CLIENT_SECRET_HEADER);
        if(StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret)) {
            ClientDetailsModel clientDetails = clientDetailsService.getByClientId(clientId);
            if (Objects.nonNull(clientDetails) && Objects.equals(clientDetails.getType(), ClientDetailsModel.CLIENT_TYPE_UNAUTH)) {
                
                
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
