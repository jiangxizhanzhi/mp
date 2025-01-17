 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CustomWebMvcConfigurer.java.
 // File size 1749.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.config.webmvc;

import cn.cyberpeace.mp.base.common.LfsCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private BaseAppConfig baseAppConfig;

    @Autowired
    private LfsCommon lfsCommon;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (Boolean.TRUE.equals(baseAppConfig.getSnakeApiMode())) {
            argumentResolvers.add(new CustomServletModelAttributeMethodProcessor(true));
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(baseAppConfig.getLfsUrlPattern())
                .addResourceLocations(String.format("file:%s%s", LfsCommon.lfsClient.getGroupDir(), FileUtil.FILE_SEPARATOR));

        //W:add swagger resource mapping
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }



}
