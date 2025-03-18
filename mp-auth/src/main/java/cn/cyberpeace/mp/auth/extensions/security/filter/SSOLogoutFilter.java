package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginOutBO;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.SSOService;
import cn.cyberpeace.mp.auth.service.SSOServiceFactory;
import cn.cyberpeace.mp.auth.util.UserUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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


    public SSOLogoutFilter(SSOServiceFactory ssoServiceFactory) {
        this.ssoServiceFactory = ssoServiceFactory;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        UserModel loginUser = UserUtils.getLoginUser();
        SSOSysEnum ssoSysEnum = SSOSysEnum.getSSOSysEnumByCode(loginUser.getOutUserType());
        if(Objects.isNull(ssoSysEnum)){
            log.warn("sso log out failed! user type is unknowns");
            return;
        }
        SSOService ssoService = ssoServiceFactory.getSSOService(ssoSysEnum);
        SSOLoginOutBO ssoLoginOutBO =
            SSOLoginOutBO.builder().ticket(loginUser.getFourATicket()).request((HttpServletRequest)request)
                .response((HttpServletResponse)response).build();
        ssoService.logout(ssoLoginOutBO);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
