package cn.cyberpeace.mp.scene.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mp.multi-scene")
@Data
public class MultiSceneConfig {

    @Value("${mp.multi-scene.manager-center-flag:false}")
    private boolean managerCenterFlag;

    @Value("${mp.multi-scene.manager-center-ip:10.100.7.1}")
    private String managerCenterIp;

    @Value("${mp.multi-scene.manager-center-port:8001}")
    private String managerCenterPort;

    @Value("${mp.multi-scene.heartbeat-interval:60}")
    private long heartbeatInterval;

    @Value("${mp.multi-scene.scan-interval:600}")
    private long scanInterval;

    @Value("${mp.multi-scene.sub-cyber-range-flag}")
    private boolean subCyberRangeFlag;

    @Value(("${mp.multi-scene.cyber-range-tag}"))
    private String cyberRangeTag;

    @Value(("${mp.multi-scene.cyber-range-ip}"))
    private String cyberRangeIp;

    @Value(("${mp.multi-scene.cyber-range-port}"))
    private String cyberRangePort;




}
