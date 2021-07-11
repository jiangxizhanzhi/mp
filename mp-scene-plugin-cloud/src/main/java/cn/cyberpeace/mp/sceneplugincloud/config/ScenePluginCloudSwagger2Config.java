package cn.cyberpeace.mp.sceneplugincloud.config;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.config.Swagger2Config;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class ScenePluginCloudSwagger2Config implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private Swagger2Config swagger2Config;

    @Bean
    public Docket apiScenePluginCloud() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.sceneplugincloud"))
                .paths(PathSelectors.any())
                .build()
                .groupName("scene-plugin-cloud");
        return docket;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Executors.newScheduledThreadPool(1).schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    var swagger = BeanCommon.getBean(InMemorySwaggerResourcesProvider.class);
                    var documentationCache = swagger.getClass().getDeclaredField("documentationCache");
                    documentationCache.setAccessible(true);
                    DocumentationCache cache = (DocumentationCache) documentationCache.get(swagger);
                    var doc = cache.documentationByGroup("scene-plugin-cloud");
                    var apiListGroup = doc.getApiListings().values();
                    for (var apiList : apiListGroup) {
                        for (var api : apiList) {
                            for (var apiDesc : api.getApis()) {
                                for (var op : apiDesc.getOperations()) {
                                    if (StringUtils.isNotEmpty(op.getNotes()) && op.getNotes().startsWith("READ_CONFIG_FROM:")) {
                                        String classPath = op.getNotes().replace("READ_CONFIG_FROM:", "");
                                        Class c = Class.forName(classPath);
                                        Field[] fields = c.getDeclaredFields();
                                        var example = new ArrayList<>(op.getRequestParameters()).get(0);
                                        for (Field field : fields) {
                                            field.setAccessible(true);
                                            String name = field.getName();
                                            String value = String.valueOf(field.get(c));
                                            RequestParameterBuilder builder = new RequestParameterBuilder();
                                            builder.name(name);
                                            builder.description(value);
                                            builder.in(example.getIn());
                                            builder.required(example.getRequired());
                                            builder.extensions(example.getExtensions());
                                            op.getRequestParameters().add(builder.build());
                                        }
                                        op.getRequestParameters().remove(example);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    
                }
            }
        }, 30, TimeUnit.SECONDS);
    }
}
