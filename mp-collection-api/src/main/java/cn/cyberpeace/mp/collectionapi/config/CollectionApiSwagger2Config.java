package cn.cyberpeace.mp.collectionapi.config;

import cn.cyberpeace.mp.base.config.Swagger2Config;
import cn.cyberpeace.mp.collection.config.CollectionSwagger2Config;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;

@Configuration
public class CollectionApiSwagger2Config {
    @Resource
    private Swagger2Config swagger2Config;

    @Bean
    public Docket apiCollectionApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.collectionapi"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(CollectionUtil.toList(CollectionSwagger2Config.authParameter))
                .groupName("collection-api");
    }

}
