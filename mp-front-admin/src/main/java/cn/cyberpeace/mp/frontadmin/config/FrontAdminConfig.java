package cn.cyberpeace.mp.frontadmin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "mp.frontadmin")
public class FrontAdminConfig {

    public static String DEFAULT_LICENSE_DIR = "/home/mp/config/license";

    private boolean activEnable = false;

    private String licenseDir = DEFAULT_LICENSE_DIR;
}
