package cn.cyberpeace.mp.flow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "mp.flow")
public class FlowConfig {

    
    private String trexImage;

    
    private String cpUrl;

}
