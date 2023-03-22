package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.CookieUtils;
import com.unionpay.common.util.PropertyUtil;
import com.unionpay.common.util.StringUtil;
import edu.yale.its.tp.cas.util.SecureHash64Util;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/3/15 17:13
 */
@Slf4j
public class SSOLogoutFilter implements Filter {

    public static final String CAS_FILTER_TOKENSTRING = "edu.yale.its.tp.cas.client.filter.tokenString";
    public static final String defaultSSOUrl = "defaultSSOUrl";
    private String defaultCasLogout;
    private String casWelcomeUrl;
    private String configFile = "";

    private UserService userService;

    public SSOLogoutFilter(UserService userService) {
        this.userService = userService;
    }

    public void init(FilterConfig config) throws ServletException {
        this.defaultCasLogout = config.getInitParameter("edu.yale.its.tp.cas.client.filter.logoutUrl");
        String ssoFile = config.getInitParameter("sso.config");
        String contextPath = config.getServletContext().getContextPath();
        if (StringUtil.trim(contextPath).equals("")) {
            this.configFile = "ssoClient.properties";
        } else {
            this.configFile = contextPath.substring(1) + ".properties";
        }

        if (!StringUtil.isBlank(ssoFile)) {
            this.configFile = ssoFile;
        }

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws ServletException, IOException {
        log.info("SSOLogoutFilter doFilter invoke...");
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpSession session = ((HttpServletRequest)request).getSession();
            HttpServletRequest req = (HttpServletRequest) request;
            String casLogout = null;
            String logoutUrl = null;

            try {
                if (session != null) {
                    session.invalidate();
                }
                logoutUrl = PropertyUtil.getInstance().getValueByDefaultFileKey(this.configFile, "defaultSSOUrl");
            } catch (Exception var13) {
                var13.printStackTrace();
            }

            if (logoutUrl != null) {
                this.defaultCasLogout = logoutUrl + "/sso/logout";
            }

            String definedSSOUrl = null;

            try {
                definedSSOUrl = PropertyUtil.getInstance().getValueByDefaultFileKey(this.configFile, request.getServerName());
            } catch (Exception var12) {
                var12.printStackTrace();
            }

            if (definedSSOUrl != null) {
                casLogout = definedSSOUrl + "/sso/logout";
                log.info("Find sso url with server \" " + request.getServerName() + " \" in " + this.configFile + " . Use defined SSO url : " + casLogout);
            } else {
                casLogout = this.defaultCasLogout;
            }


            String originalService = this.getService(req);
            String serviceEncrypted = null;

            log.info("originalService======{}",originalService);
            String targetUrl;
            try {
                targetUrl = URLDecoder.decode(originalService);
                serviceEncrypted = SecureHash64Util.obfusecate(targetUrl);
            } catch (Exception var11) {
                var11.printStackTrace();
            }

            targetUrl = casLogout + "?service=" + originalService + "&flag=" + serviceEncrypted;
            log.info("targetUrl======{}",targetUrl);
            ((HttpServletResponse)response).sendRedirect(targetUrl);

        } else {
            throw new ServletException("CASFilter protects only HTTP resources");
        }
    }

    public void destroy() {
    }

    private String getService(HttpServletRequest request) throws ServletException {
        this.casWelcomeUrl = null;
        String casServerName = null;
        int port = request.getServerPort();
        log.info("requestServername============="+request.getServerName());
        log.info("requestServerport============="+request.getServerPort());
        log.info("port============="+port);
        if (port != 80 && port != 443) {
            casServerName = request.getServerName() + ":" + request.getServerPort();
        } else {
            casServerName = request.getServerName();
        }

        log.info("casServerName============="+casServerName);
        log.info("request.getContextPath()========"+request.getContextPath());
        casServerName = request.getServerName();
        log.info("test===updatefor====ssourl===="+casServerName);

        if (casServerName == null && this.casWelcomeUrl == null) {
            throw new ServletException("need one of the following configuration parameters: edu.yale.its.tp.cas.client.filter.welcomeUrl or edu.yale.its.tp.cas.client.filter.serverName");
        } else {
            if (this.casWelcomeUrl == null) {
                StringBuffer sb = new StringBuffer();
                if (request.isSecure()) {
                    sb.append("https://");
                } else {
                    sb.append("http://");
                }

                sb.append(casServerName);
                //TODO 设置为首页进行测试
                sb.append("/page/mg/login");
                log.info("loginpage==="+sb.toString());
                //sb.append(request.getContextPath());
                this.casWelcomeUrl = URLEncoder.encode(sb.toString());
            }
            log.info("this.casWelcomeUrl==="+this.casWelcomeUrl);

            return this.casWelcomeUrl;
        }
    }
}
