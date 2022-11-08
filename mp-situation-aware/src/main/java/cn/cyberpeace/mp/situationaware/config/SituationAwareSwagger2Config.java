package cn.cyberpeace.mp.situationaware.config;

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
public class SituationAwareSwagger2Config {

    @Autowired
    private Swagger2Config swagger2Config;

    @Bean
    public Docket apiSituationAware() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.situationaware"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(CollectionUtil.toList(AuthSwagger2Config.authParameter))
                .groupName("situation-aware");
    }

}
