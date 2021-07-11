package cn.cyberpeace.mp.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "mp.log")
public class LogConfig {

    
    private List<String> excludeUrls;

    
    private List<String> excludeMethods;

    
    private String esNodes;

    
    private String cpUrl;

    
    private String mpIp;

    
    private String logDb;

    
    private Integer esMaxPageSize;
}
