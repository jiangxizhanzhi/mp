package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginOutBO;
import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.extensions.security.event.UserEventPublisher;
import cn.cyberpeace.mp.auth.extensions.security.event.UserLogoutEvent;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.SSOService;
import cn.cyberpeace.mp.auth.service.SSOServiceFactory;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.CookieUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.extensions.security.filter
 * @Author: hhuang
 * @CreateTime: 2025/03/18  16:57
 * @Description: 单点登出过滤器
 * @Version: 1.0
 */
@Slf4j
public class SSOLogoutFilter implements Filter {


    private SSOServiceFactory ssoServiceFactory;

    private AuthConfig authConfig;

    private UserService userService;

    private UserEventPublisher userEventPublisher;



    public SSOLogoutFilter(SSOServiceFactory ssoServiceFactory,AuthConfig authConfig,UserService userService,UserEventPublisher userEventPublisher) {
        this.ssoServiceFactory = ssoServiceFactory;
        this.authConfig = authConfig;
        this.userService = userService;
        this.userEventPublisher = userEventPublisher;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if(request instanceof HttpServletRequest && response instanceof HttpServletResponse){
            HttpServletRequest httpServletRequest = (HttpServletRequest)request;
            HttpSession session = httpServletRequest.getSession();

            if (session != null) {
                session.invalidate();
            }
            //jwt token
            String crJwtToken = CookieUtils.getCookie(httpServletRequest, "cr_jwttoken");
            if(StringUtils.isEmpty(crJwtToken)){
                log.warn("sso log out failed! user cookie cr_jwttoken is empty!");
                return;
            }
            //用户信息获取
            UserModel loginUser = JwtCommon.verifyToken(crJwtToken);
            if (Objects.isNull(loginUser)) {
                log.warn("sso log out failed! user is not exist!");
                return;
            }
            //重置秘钥
            if (authConfig.getLogoutResetSecret()) {
                if (!StringUtils.equals(loginUser.getId(), authConfig.getLogoutKeepSecretUser())) {
                    // 退出是否重置秘钥
                    UserModel updateUser = new UserModel();
                    updateUser.setId(loginUser.getId());
                    updateUser.initDefault("tokenSecret");
                    userService.update(updateUser);
                }
            }
            SSOSysEnum ssoSysEnum = SSOSysEnum.getSSOSysEnumByCode(loginUser.getOutUserType());
            if(Objects.isNull(ssoSysEnum)){
                log.warn("sso log out failed! user type is unknown");
                return;
            }
            //用户登出事件发布
            userEventPublisher.publishEvent(new UserLogoutEvent(JsonCommon.beanToMap(loginUser)));
            SSOService ssoService = ssoServiceFactory.getSSOService(ssoSysEnum);
            SSOLoginOutBO ssoLoginOutBO =
                SSOLoginOutBO.builder().ticket(loginUser.getFourATicket()).request(httpServletRequest)
                    .response((HttpServletResponse)response).build();
            //单点登出处理
            ssoService.logout(ssoLoginOutBO);
        }else {
            throw new ServletException("CASFilter protects only HTTP resources");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
