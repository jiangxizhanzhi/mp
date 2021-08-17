package cn.cyberpeace.mp.devops.cluster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 对接云平台配置类
 */
@Configuration
@ConfigurationProperties(prefix = "mp.devops",ignoreInvalidFields = true)
@Data
public class DevopsConfig {

    /**
     * 云平台接口地址
     */
    private String cpUrl;

    /**
     * cr接口地址
     */
    private String crUrl;

}
