package cn.cyberpeace.mp.flow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author shgang
 * @Date 2022/8/31
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.flow")
public class FlowConfig {

    /**
     * trex镜像名称
     */
    private String trexImage;

    /**
     * 云平台地址
     */
    private String cpUrl;

}
