package cn.cyberpeace.mp.cscene.config;

import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.front.admin.service.sys.SysActivationService;
import cn.cyberpeace.mp.front.admin.service.sys.SysConfigService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.cscene")
@Data
public class CsceneAppConfig {

    @Autowired
    private SysActivationService sysActivationService;

    @Autowired
    private SysConfigService sysConfigService;

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

    @Value("${mp.cscene.has_own_recording_set:213,214,236}")
    private String hasOwnRecordingSet;

    @Autowired
    private MessageSource messageSource;

    @PostConstruct
    public void addBaseNames() {
        ReloadableResourceBundleMessageSource msgSrc = (ReloadableResourceBundleMessageSource) messageSource;
        msgSrc.addBasenames("classpath:i18n/cscene");
    }

    public int getSceneEditNodeLimit() {
        String value = null;
        try {
            value = sysActivationService.getValueByKey(sceneEditNodeLimitKey);
        } catch (Exception e) {
            log.error("Get config {} error: {}", sceneEditNodeLimitKey, e);
        }
        if (value == null) {
            return sceneEditNodeLimit;
        }

        var limit = IntUtils.fromStr(value);
        if (limit.equals(0)) {
            log.warn("SceneEditNodeLimit({}) parse as 0", value);
        }
        return limit;
    }

    public int getSceneUsingNodeLimit() {
        String value = null;
        try {
            value = sysActivationService.getValueByKey(sceneUsingNodeLimitKey);
        } catch (Exception e) {
            log.error("Get config {} error: {}", sceneUsingNodeLimitKey, e);
        }
        if (value == null) {
            return sceneUsingNodeLimit;
        }

        var limit = IntUtils.fromStr(value);
        if (limit.equals(0)) {
            log.warn("SceneUsingNodeLimit({}) parse as 0", value);
        }
        return limit;
    }

    public boolean getSceneEnableRecording() {
        var config = sysConfigService.getByKeyName(sceneEnableRecordingKey);
        if (config == null) {
            return sceneEnableRecording;
        }

        var value = config.getValue();
        return BooleanUtils.toBoolean(value);
    }

    public Set<Integer> getHasOwnRecordingSet() {
        String[] values = this.hasOwnRecordingSet.split(",");
        Set<Integer> set = new HashSet<>();
        Arrays.stream(values).forEach(v -> set.add(Integer.parseInt(v)));
        return set;
    }
}
