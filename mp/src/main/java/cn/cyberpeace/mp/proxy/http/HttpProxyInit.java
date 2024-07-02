package cn.cyberpeace.mp.proxy.http;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.proxy.ProxyConfig;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.List;

/**
 * @ClassName HttpProxyInit
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ServletContextInitializer
 */
@Component
public class HttpProxyInit implements ServletContextInitializer {

    @Autowired
    private ProxyConfig proxyConfig;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        List<HttpProxyConfig> http = proxyConfig.getHttp();
        if (http == null || http.isEmpty()) {
            return;
        }

        for (HttpProxyConfig httpProxyConfig : http) {
            ServletRegistration.Dynamic dispatcher = servletContext.addServlet(TextUtils.uuid(), new ProxyServlet());
            dispatcher.addMapping(httpProxyConfig.getUrl());
            dispatcher.setInitParameter(ProxyServlet.P_TARGET_URI, httpProxyConfig.getTargetUrl());
            dispatcher.setInitParameter(ProxyServlet.P_LOG, httpProxyConfig.getLogEnabled());
            dispatcher.setLoadOnStartup(1);
        }
    }
}
