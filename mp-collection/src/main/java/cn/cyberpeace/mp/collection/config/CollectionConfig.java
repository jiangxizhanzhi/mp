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
}
