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

    /**
     * moloch es节点地址
     */
    private String wazuhEsNodes;

    /**
     * MP新中台ES地址
     */
    private String arkimeEsNodes;

    /**
     *
     */
    private String esDatePattern;

    /**
     * monitors 服务监控
     */
    private List<MonitorProbe> cpSrvMonitors;
    private List<MonitorProbe> bizSrvMonitors;

    /**
     * arkime
     */
    private String arkimeCpcloudContainerCreateUrl;
    private String arkimeCpcloudNetworkMirrorUrl;
    private String arkimeEsHost;
    private String arkimeEsPort;
    private String arkimeImageName;
    private String arkimeUsername;
    private String arkimePassword;

    /**
     * wazuh 开放接口
     */
    private String wazuhBaseUrl;
    private String wazuhUsername;
    private String wazuhPassword;
    private String wazuhFlowTableUrl;
    /**
     * wazuh-probe-manager
     */
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

        /**
         * 服务列表
         */
        private String serviceList;

        /**
         * 该列表中的服务：正常状态下不允许重启，异常状态下才允许重启
         */
        private List<String> restartRestrictList;
    }

}
