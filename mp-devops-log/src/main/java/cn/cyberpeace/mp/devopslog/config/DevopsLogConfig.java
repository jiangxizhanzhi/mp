package cn.cyberpeace.mp.devopslog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/8/31
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.devopslog")
public class DevopsLogConfig {

    /**
     * 不希望记录日志的URL Pattern
     */
    private List<String> excludeUrls;

    /**
     * 不需要记录日志的请求method。譬如通常可以忽略的method: OPTIONS
     */
    private List<String> excludeMethods;

    /**
     * MP新中台ES地址
     */
    private String esNodes;

    /**
     * 云平台地址
     */
    private String cpUrl;

    /**
     * MP中台地址
     */
    private String mpIp;

    /**
     * #日志存储数据库：es、mysql
     */
    private String logDb;
}
