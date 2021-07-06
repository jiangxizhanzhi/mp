package cn.cyberpeace.mp.auth.config;


import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "mp.auth")
public class AuthConfig {

    @DefaultBool(value = false)
    private boolean needRegistrationAudit;

    private String excelTemplateName;

    private String excelTemplatePath;

}
