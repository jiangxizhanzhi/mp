 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LfProxyInit.java.
 // File size 1057.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.proxy.lf;

import cn.cyberpeace.mp.proxy.ProxyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @ClassName LfProxyInit
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see WebMvcConfigurer
 */
@Component
public class LfProxyInit implements WebMvcConfigurer {

    @Autowired
    private ProxyConfig proxyConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        List<LfProxyConfig> lf = proxyConfig.getLf();
        if (lf == null || lf.isEmpty()) {
            return;
        }

        for (LfProxyConfig lfProxyConfig : lf) {
            registry.addResourceHandler(lfProxyConfig.getUrl()).addResourceLocations(lfProxyConfig.getLocation());
        }
    }

}
