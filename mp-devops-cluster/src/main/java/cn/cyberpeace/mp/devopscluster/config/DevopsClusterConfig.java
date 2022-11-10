package cn.cyberpeace.mp.devopscluster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * 对接云平台配置类
 * @author wanggang
 */
@Configuration
@ConfigurationProperties(prefix = "mp.devopscluster",ignoreInvalidFields = true)
@Data
public class DevopsClusterConfig {

    /**
     * 云平台接口地址
     */
    private String cpUrl;

    /**
     * 磁盘空间告警阈值
     */
    private Integer diskAlarmPercent;

    /**
     * 虚拟网络已使用IP占比告警阈值
     */
    private Integer netIPUsageAlarmPercent;

    /**
     * 虚拟网络IO占比告警阈值
     */
    private Integer netIOUsageAlarmPercent;

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

    /**
     * 公网部署不需要替换ip的协议
     */
    private List<String> publicNetProxyExcludeProtocols;

    /**
     * 主机名称
     */
    private String hostname;

    /**
     * 承诺最大支持靶标的磁盘大小(G)
     */
    private String maxTargetSize;

    /**
     * 云平台接口IP
     */
    private String cpIp;

    /**
     * 云平台端口
     */
    private String cpPort;

    /**
     * MP接口IP
     */
    private String mpIp;

    /**
     * MP端口
     */
    private String mpPort;

}
