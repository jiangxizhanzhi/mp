package cn.cyberpeace.mp.base.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 针对返回自定义类型
 * 统一返回结果风格，例如：驼峰命名字段转换成蛇形命名；字段值为null的字段不进行返回
 */
@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CustomServletModelAttributeMethodProcessor(true));
    }
}
