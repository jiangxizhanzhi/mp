 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogSwagger2Config.java.
 // File size 1760.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.config;

import cn.cyberpeace.mp.base.config.Swagger2Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

/**
 * @ClassName LogSwagger2Config
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Configuration
public class LogSwagger2Config {

    public static RequestParameter authParameter = new RequestParameterBuilder()
            .name("Authorization")
            .description("认证")
            .in(ParameterType.HEADER)
            .query(q -> q.allowEmptyValue(true))
            .required(false)
            .build();

    @Autowired
    private Swagger2Config swagger2Config;

    @Bean
    public Docket apiLog() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.log"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(Collections.singletonList(authParameter))
                .groupName("log");
    }

}
