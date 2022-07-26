package cn.cyberpeace.mp.base.config.webmvc;

import cn.cyberpeace.mp.base.common.LfsCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 针对返回自定义类型
 * 统一返回结果风格，例如：驼峰命名字段转换成蛇形命名；字段值为null的字段不进行返回
 * 添加http请求拦截器
 */
@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private BaseAppConfig baseAppConfig;

    @Autowired
    private LfsCommon lfsCommon;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CustomServletModelAttributeMethodProcessor(true));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(baseAppConfig.getLfsUrlPattern())
                .addResourceLocations(String.format("file:%s%s", LfsCommon.lfsClient.getGroupDir(), FileUtil.FILE_SEPARATOR));
    }

}
