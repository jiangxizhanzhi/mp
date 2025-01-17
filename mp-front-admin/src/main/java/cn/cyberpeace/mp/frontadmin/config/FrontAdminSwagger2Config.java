 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FrontAdminSwagger2Config.java.
 // File size 1289.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.config;

import cn.cyberpeace.mp.auth.config.AuthSwagger2Config;
import cn.cyberpeace.mp.base.config.Swagger2Config;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class FrontAdminSwagger2Config {

    @Autowired
    private Swagger2Config swagger2Config;

    @Bean
    public Docket apiFrontAdmin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.frontadmin"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(CollectionUtil.toList(AuthSwagger2Config.authParameter))
                .groupName("front-admin");
    }

}
