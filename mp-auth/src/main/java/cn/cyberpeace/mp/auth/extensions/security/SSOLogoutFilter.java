package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import com.unionpay.common.util.PropertyUtil;
import com.unionpay.common.util.StringUtil;
import edu.yale.its.tp.cas.util.SecureHash64Util;

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

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws ServletException, IOException {
        if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
            HttpServletRequest request = (HttpServletRequest) req;

            UserModel user = JwtCommon.verifyToken(request);
            if (user != null) {
                var updateUser = new UserModel();
                updateUser.setId(user.getId());
                updateUser.initDefault("tokenSecret");
                userService.update(updateUser);
                //recordLogoutLog(user, request);
            }

            // 清空session
            //HttpSession session = request.getSession(false);
            //if (session != null) {
            //    session.invalidate();
            //}


            HttpSession session = request.getSession();
            String casLogout = null;
            String logoutUrl = null;

            try {
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
                System.out.println("Find sso url with server \" " + request.getServerName() + " \" in " + this.configFile + " . Use defined SSO url : " + casLogout);
            } else {
                casLogout = this.defaultCasLogout;
            }

            if (session != null) {
                session.invalidate();
            }

            String originalService = this.getService((HttpServletRequest)request);
            String serviceEncrypted = null;

            String targetUrl;
            try {
                targetUrl = URLDecoder.decode(originalService);
                serviceEncrypted = SecureHash64Util.obfusecate(targetUrl);
            } catch (Exception var11) {
                var11.printStackTrace();
            }

            targetUrl = casLogout + "?service=" + originalService + "&flag=" + serviceEncrypted;
            ((HttpServletResponse)res).sendRedirect(targetUrl);
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
        if (port != 80 && port != 443) {
            casServerName = request.getServerName() + ":" + request.getServerPort();
        } else {
            casServerName = request.getServerName();
        }

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
                sb.append(request.getContextPath());
                this.casWelcomeUrl = URLEncoder.encode(sb.toString());
            }

            return this.casWelcomeUrl;
        }
    }
}
