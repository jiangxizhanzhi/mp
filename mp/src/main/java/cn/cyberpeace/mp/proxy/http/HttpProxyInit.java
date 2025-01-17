 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HttpProxyInit.java.
 // File size 1344.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
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

 /**
  * HTTP代理初始化
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class HttpProxyInit implements ServletContextInitializer {

     /**
      * 代理配置
      */
     @Autowired
    private ProxyConfig proxyConfig;

     /**
      * 在启动时
      *
      * @param servletContext servlet上下文
      * @throws ServletException servlet异常
      */
     @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        var http = proxyConfig.getHttp();
        if (http == null || http.isEmpty()) {
            return;
        }

        for (var httpProxyConfig : http) {
            ServletRegistration.Dynamic dispatcher = servletContext.addServlet(TextUtils.uuid(), new ProxyServlet());
            dispatcher.addMapping(httpProxyConfig.getUrl());
            dispatcher.setInitParameter(ProxyServlet.P_TARGET_URI, httpProxyConfig.getTargetUrl());
            dispatcher.setInitParameter(ProxyServlet.P_LOG, httpProxyConfig.getLogEnabled());
            dispatcher.setLoadOnStartup(1);
        }
    }
}
