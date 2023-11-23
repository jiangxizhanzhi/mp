package cn.cyberpeace.mp.collection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "mp.situationaware", ignoreInvalidFields = true)
public class CollectionConfig {
    /**
     * es 节点地址
     */
    private String esNode;

    /**
     * 上层业务层服务地址
     */
    private String app;

    private String adApp;
}
