package cn.cyberpeace.mp.devopscluster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 对接云平台配置类
 * @author wanggang
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
     * 磁盘空间告警阈值
     */
    private Integer diskAlarmPercent;

    private Boolean publicNetProxyServer;

    /**
     * 公网开关
     */
    private Boolean publicNetProxy;

    /**
     * 公网ip
     */
    private String publicNetProxyIp;

    /**
     * 公网端口
     */
    private Integer publicNetProxyPort;
}
