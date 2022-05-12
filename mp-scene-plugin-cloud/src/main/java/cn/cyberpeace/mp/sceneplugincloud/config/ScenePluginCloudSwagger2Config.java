package cn.cyberpeace.mp.sceneplugincloud.config;

import cn.cyberpeace.mp.base.config.Swagger2Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class ScenePluginCloudSwagger2Config {

    @Autowired
    private Swagger2Config swagger2Config;

    @Bean
    public Docket apiScenePluginCloud() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swagger2Config.apiInfo())
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.cyberpeace.mp.sceneplugincloud"))
                .paths(PathSelectors.any())
                .build()
                .groupName("scene-plugin-cloud");
    }

}
