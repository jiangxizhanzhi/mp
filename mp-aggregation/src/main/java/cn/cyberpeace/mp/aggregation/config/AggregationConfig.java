package cn.cyberpeace.mp.aggregation.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "spring")
@Data
public class AggregationConfig {

    @Value("${spring.web.resources.static-locations}")
    private String staticLocation;

    @Value("${mp.aggregation.export-image-timeout:3600000}")
    private Integer exportImageTimeout;

}
