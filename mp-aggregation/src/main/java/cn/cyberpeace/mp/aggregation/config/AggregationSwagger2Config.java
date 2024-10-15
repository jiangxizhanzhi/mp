 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AggregationSwagger2Config.java.
 // File size 1472.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.config;

import cn.cyberpeace.mp.base.config.AuthSwagger2Config2;
import cn.cyberpeace.mp.base.config.Swagger2Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

/**
 * @ClassName AggregationSwagger2Config
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Configuration
public class AggregationSwagger2Config {

    @Autowired
    private Swagger2Config swagger2Config;

    /**
     * apiAggregation
     *
     * @return
     */
    @Bean
    public Docket apiAggregation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.aggregation"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(Collections.singletonList(AuthSwagger2Config2.authParameter))
                .groupName("aggregation");
    }

}
