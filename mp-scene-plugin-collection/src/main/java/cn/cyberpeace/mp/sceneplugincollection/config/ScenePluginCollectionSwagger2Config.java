 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScenePluginCollectionSwagger2Config.java.
 // File size 1351.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.base.config.Swagger2Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @ClassName ScenePluginCollectionSwagger2Config
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Configuration
public class ScenePluginCollectionSwagger2Config {

    @Autowired
    private Swagger2Config swagger2Config;

    /**
     * apiScenePluginCollection
     *
     * @return
     */
    @Bean
    public Docket apiScenePluginCollection() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.sceneplugincollection"))
                .paths(PathSelectors.any())
                .build()
                .groupName("scene-plugin-collection");
    }

}
