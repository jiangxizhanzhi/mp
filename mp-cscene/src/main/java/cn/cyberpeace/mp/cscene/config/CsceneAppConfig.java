package cn.cyberpeace.mp.cscene.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.cscene")
@Data
public class CsceneAppConfig {

    /**
     * 云平台适配层接口地址
     */
    private String cpIp;

    @Value("${mp.cscene.scene-edit-node-limit-key:LCS-RES-CR-EDITNODES}")
    private String sceneEditNodeLimitKey;

    @Value("${mp.cscene.scene-using-node-limit-key:LCS-RES-CR-ACTIVENODES}")
    private String sceneUsingNodeLimitKey;

    /**
     * TODO: 正确的key
     */
    @Value("${mp.cscene.scene-enable-recording-key:screen_recording}")
    private String sceneEnableRecordingKey;

    @Value("${mp.cscene.scene-edit-node-limit:1000}")
    private Integer sceneEditNodeLimit;

    @Value("${mp.cscene.scene-using-node-limit:1000}")
    private Integer sceneUsingNodeLimit;

    @Value("${mp.cscene.scene-enable-recording:false}")
    private Boolean sceneEnableRecording;

    @Value("${mp.cscene.scene-message-timeout:3600000}")
    private Long sceneMessageTimeout;

    @Value("${mp.cscene.has-own-recording-set:213,214,236}")
    private String hasOwnRecordingSet;

    @Value("${mp.cscene.scene-error-creating-seconds:3600}")
    private Integer sceneErrorCreatingSeconds;

    @Value("${mp.cscene.element-error-creating-seconds:300}")
    private Integer elementErrorCreatingSeconds;

    @Value("${mp.cscene.ignore-scene-creating-seconds:60}")
    private Integer ignoreSceneCreatingSeconds;

    @Value("${mp.cscene.enable-scene-cron-job:true}")
    private Boolean enableSceneCronJob;

    @Value("${mp.cscene.enable-command-change:false}")
    private Boolean enableCommandChange;

    @Value("${mp.cscene.cron-job-expression:0 0/15 * * * ?}")
    private String cronJobExpression;

    @Value("${mp.cscene.enable_node_limit_db_check:true}")
    private Boolean enableNodeLimitDbCheck;

    @Autowired
    private MessageSource messageSource;

    public int getSceneEditNodeLimit() {
        return 1000;
    }

    public int getSceneUsingNodeLimit() {
        return 1000;
    }

    public boolean getSceneEnableRecording() {
        return true;
    }

    public Set<Integer> getHasOwnRecordingSet() {
        String[] values = this.hasOwnRecordingSet.split(",");
        Set<Integer> set = new HashSet<>();
        Arrays.stream(values).forEach(v -> set.add(Integer.parseInt(v)));
        return set;
    }
}
