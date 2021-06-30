/*
 * Copyright (c) 2018. 南京云帐房网络科技有限公司 版权所有
 */

package cn.cyberpeace.mp.base.config;

import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableKnife4j
@EnableSwagger2WebMvc
public class Swagger2Config {

    @Bean
    public Docket createRestApi() {
        //在配置好的配置类中增加此段代码即可
//        RequestParameterBuilder ticketPar = new RequestParameterBuilder().b;
//        List<RequestParameter> pars = new ArrayList<>();
//        ticketPar.name("Authorization").description("登录校验")//name表示名称，description表示描述
//                .modelRef(new ModelRef("string")).parameterType("header")
//                .required(false).defaultValue(null).build();//required表示是否必填，defaultvalue表示默认值
//        pars.add(ticketPar.build());//添加完此处一定要把下边的带***的也加上否则不生效

        RequestParameter parameter =
                new RequestParameterBuilder()
                        .name("Authorization")
                        .description("认证")
                        .in(ParameterType.HEADER)
                        .required(false)
                        .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(CollectionUtil.toList(parameter));

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("mp swagger doc")
                .description("mp swagger doc")
                .termsOfServiceUrl("")
                .version("1.0")
                .build();
    }


}
