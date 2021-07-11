package cn.cyberpeace.mp.devopscluster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;



@Configuration
@ConfigurationProperties(prefix = "mp.devopscluster",ignoreInvalidFields = true)
@Data
public class DevopsClusterConfig {

    
    private String cpUrl;

    
    private Integer diskAlarmPercent;

    private Boolean publicNetProxyServer;

    
    private Boolean publicNetProxy;

    
    private String publicNetProxyIp;

    
    private Integer publicNetProxyPort;

    
    private List<String> publicNetProxyExcludeProtocols;

    
    private String hostname;

    
    private String maxTargetSize;

    
    private String cpIp;

    
    private String cpPort;

    
    private String mpIp;

    
    private String mpPort;

    
    private Boolean allowExport;
}
