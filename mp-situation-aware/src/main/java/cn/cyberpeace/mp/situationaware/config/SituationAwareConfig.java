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
     * 分页最大总数 max_result_window
     */
    private Integer wazuhEsMaxPageSize;

    private String publicWazuhEsNodes;
    private Integer publicWazuhEsMaxPageSize;
    private String privateWazuhEsNodes;
    private Integer privateWazuhEsMaxPageSize;
    private String mixWazuhEsNodes;
    private Integer mixWazuhEsMaxPageSize;

    /**
     * MP新中台ES地址
     */
    private String arkimeEsNodes;

    /**
     *
     */
    private String esDatePattern;

    /**
     * monitors
     */
    private String monitorCpCloudInterval;
    private String monitorBusinessInterval;
    private List<String> monitorCpCloudServerList;
    private List<String> monitorBusinessServerList;
    private List<String> monitorCpCloudRestartRestrict;
    private List<String> monitorBusinessRestartRestrict;

    /**
     * arkime
     */
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

    /**
     * wazuh
     */
    private String wazuhBaseUrl;
    private String wazuhUsername;
    private String wazuhPassword;
    private String wazuhFlowTableUrl;
    /**
     * public-wazuh
     */
    private String publicWazuhBaseUrl;
    private String publicWazuhUsername;
    private String publicWazuhPassword;
    /**
     * private-wazuh
     */
    private String privateWazuhBaseUrl;
    private String privateWazuhUsername;
    private String privateWazuhPassword;
    /**
     * mix-wazuh
     */
    private String mixWazuhBaseUrl;
    private String mixWazuhUsername;
    private String mixWazuhPassword;
    /**
     * management-image
     */
    private String publicManagementImageId;
    private String privateManagementImageId;
    private String mixManagementImageId;
    private String managementUsername;
    private String managementPassword;


    /**
     * wazuh-probe-manager
     */
    private String probeMgrUrl;
    private String probeMgrAgentScriptLinuxPath;
    private String probeMgrAgentScriptWindowsPath;
    private String probeMgrTaskEnable;

    private String groupNamePrefix;

    private String mixAzureManagerUrl;

    private String privateAzureManagerUrl;

    private String millionScene;

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
