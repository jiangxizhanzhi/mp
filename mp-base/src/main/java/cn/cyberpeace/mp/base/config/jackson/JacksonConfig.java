package cn.cyberpeace.mp.base.config.jackson;

import cn.cyberpeace.mp.base.util.BaseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.jackson")
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return BaseUtils.createSnakeObjectMapper();
    }

}
