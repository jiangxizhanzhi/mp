 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LfProxyInit.java.
 // File size 866.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.proxy.lf;

import cn.cyberpeace.mp.proxy.ProxyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

 /**
  * Lf代理初始化
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class LfProxyInit implements WebMvcConfigurer {

     /**
      * 代理配置
      */
     @Autowired
    private ProxyConfig proxyConfig;

     /**
      * 添加资源处理程序
      *
      * @param registry 注册表
      */
     @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var lf = proxyConfig.getLf();
        if (lf == null || lf.isEmpty()) {
            return;
        }

        for (var lfProxyConfig : lf) {
            registry.addResourceHandler(lfProxyConfig.getUrl()).addResourceLocations(lfProxyConfig.getLocation());
        }
    }

}
