package cn.cyberpeace.mp.base.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mp.base")
@Data
public class BaseAppConfig {

    @Value("${mp.base.default-page-size:10}")
    private Integer defaultPageSize;

    @Value("${mp.base.max-page-size:1000}")
    private Integer maxPageSize;

}
