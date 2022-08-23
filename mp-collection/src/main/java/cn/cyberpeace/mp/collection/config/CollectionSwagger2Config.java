package cn.cyberpeace.mp.collection.config;

import cn.cyberpeace.mp.base.config.Swagger2Config;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;

@Configuration
public class CollectionSwagger2Config {

    public static RequestParameter authParameter = new RequestParameterBuilder()
            .name("Authorization")
            .description("认证")
            .in(ParameterType.HEADER)
            .query(q -> q.allowEmptyValue(true))
            .required(false)
            .build();

    @Resource
    private Swagger2Config swagger2Config;

    @Bean
    public Docket apiCollection() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.collection"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(CollectionUtil.toList(authParameter))
                .groupName("collection");
    }

}
