package cn.cyberpeace.mp.frontbusiness.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "mp.frontbusiness")
public class FrontBusinessConfig {

    
    private Boolean urlAcFilterEnabled = true;

    
    private Boolean urlAcFilterMethodMatch = true;

    
    private Boolean urlAcFilterAuthorityMatch = true;

    
    private Boolean urlAcFilterIgnoreIfUnauth = false;

    
    private Boolean urlAcFilterIgnoreIfNotDefined = false;

    
    private List<String> urlAcFilterExcludeUrls;
}
