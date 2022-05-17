package cn.cyberpeace.mp.devops.cluster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 对接云平台配置类
 * @author wanggang
 */
@Configuration
@ConfigurationProperties(prefix = "ip",ignoreInvalidFields = true)
@Data
public class IpAddressConfig {

    /**
     * 云平台接口IP
     */
    private String cp;

    /**
     * MP接口IP
     */
    private String mp;

}
