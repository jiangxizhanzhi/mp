package cn.cyberpeace.mp.base.config.webmvc;

import cn.cyberpeace.mp.base.common.LfsCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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

        //W:add swagger resource mapping
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/mp/load/load/updateLoadExecutionOutResult")
                .allowedOrigins("/*")  // 这里替换成实际允许的前端源地址，可以用通配符*允许所有源，但不太安全，建议指定具体源
                .allowedMethods("POST")  // 根据实际需求指定允许的HTTP方法
                .allowedHeaders("*")  // 允许所有请求头，也可以按需指定具体的请求头
                .maxAge(3600);  // 预检请求的缓存时间，单位秒
    }


}
