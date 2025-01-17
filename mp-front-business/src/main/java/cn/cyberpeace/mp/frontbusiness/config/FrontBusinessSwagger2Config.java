 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FrontBusinessSwagger2Config.java.
 // File size 1304.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.config;

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

 /**
  * 前面业务swagger2配置
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Configuration
public class FrontBusinessSwagger2Config {

     /**
      * swagger2配置
      */
     @Autowired
    private Swagger2Config swagger2Config;

     /**
      * API前端业务
      *
      * @return {@link Docket }
      */
     @Bean
    public Docket apiFrontBusiness() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.frontbusiness"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(CollectionUtil.toList(AuthSwagger2Config.authParameter))
                .groupName("front-business");
    }

}
