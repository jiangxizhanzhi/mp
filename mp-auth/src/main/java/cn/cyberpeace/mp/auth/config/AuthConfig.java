package cn.cyberpeace.mp.auth.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "mp.auth")
public class AuthConfig {

    private String excelTemplateName;

    private String excelTemplatePath;

}
