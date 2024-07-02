package cn.cyberpeace.mp.common.config;

import cn.cyberpeace.mp.base.config.Swagger2Config;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
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
 * @ClassName CommonSwagger2Config
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Configuration
public class CommonSwagger2Config {

    public static RequestParameter authParameter = new RequestParameterBuilder()
            .name("Authorization")
            .description("认证")
            .in(ParameterType.HEADER)
            .query(q -> q.allowEmptyValue(true))
            .required(false)
            .build();

    @Autowired
    private Swagger2Config swagger2Config;

    /**
     * apiBase
     *
     * @return
     */
    @Bean
    public Docket apiBase() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors
                              .basePackage("cn.cyberpeace.mp.common")
                              .or(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.base")))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(Collections.singletonList(authParameter))
                .groupName("base");
    }

}
