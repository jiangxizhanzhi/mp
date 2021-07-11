package cn.cyberpeace.mp.situationaware.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@Data
@ConfigurationProperties(prefix = "mp.situationaware", ignoreInvalidFields = true)
public class SituationAwareConfig {

    
    private String wazuhEsNodes;

    
    private Integer wazuhEsMaxPageSize;

    
    private String arkimeEsNodes;

    
    private String esDatePattern;

    
    private String monitorCpCloudInterval;
    private String monitorBusinessInterval;
    private List<String> monitorCpCloudServerList;
    private List<String> monitorBusinessServerList;
    private List<String> monitorCpCloudRestartRestrict;
    private List<String> monitorBusinessRestartRestrict;

    
    private String arkimeCpcloudContainerCreateUrl;
    private String arkimeCpcloudNetworkMirrorUrl;
    private String arkimeEsProtocol;
    private String arkimeEsHost;
    private String arkimeEsPort;
    private String arkimeImageName;
    private Integer arkimeDisk;
    private String arkimeUsername;
    private String arkimePassword;

    private Boolean entityDst;
    private String entityDstNode;

    
    private String wazuhBaseUrl;
    private String wazuhUsername;
    private String wazuhPassword;
    private String wazuhFlowTableUrl;
    
    private String probeMgrUrl;
    private String probeMgrAgentScriptLinuxPath;
    private String probeMgrAgentScriptWindowsPath;
    private String probeMgrTaskEnable;

    public static DateTimeFormatter esDateTimeFormatter = null;
    @PostConstruct
    public void init() {
        esDateTimeFormatter = DateTimeFormatter.ofPattern(esDatePattern);
    }

    @Data
    public static class MonitorProbe {

        private String probeName;

        private String launchId;

        private String interval;

        
        private String serviceList;

        
        private List<String> restartRestrictList;
    }

}
