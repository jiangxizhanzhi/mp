package cn.cyberpeace.mp.base.config.webmvc;

import cn.cyberpeace.mp.base.common.LfsCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private BaseAppConfig baseAppConfig;

    @Autowired
    private LfsCommon lfsCommon;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (Boolean.TRUE.equals(baseAppConfig.getSnakeApiMode())) {
            argumentResolvers.add(new CustomServletModelAttributeMethodProcessor(true));
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(baseAppConfig.getLfsUrlPattern())
                .addResourceLocations(String.format("file:%s%s", LfsCommon.lfsClient.getGroupDir(), FileUtil.FILE_SEPARATOR));

        
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }



}
