package cn.cyberpeace.mp.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "mp.auth")
public class AuthConfig {
    
    private Boolean logoutResetSecret = true;

    private String logoutKeepSecretUser;

    
    private Integer maxImportUserSize;
    
    private String defaultUserAvatar;

    private Integer authenticateCacheTime;

    private Boolean authenticateUseIndividualTokenSecret;

    private String authenticateJwtSecret;

    private Long authenticateJwtExpires;

    
    private List<String> authenticateUrls;

    
    private List<String> authenticateExcludeUrls;

    
    private Integer authenticateEncoderIterations;

    
    private Integer authenticateEncoderHashWidth;

    
    private Long mqMsgTimeout;
}
