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
import org.springframework.context.annotation.Configuration;

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
    @Value("${mp.cscene.scene-enable-recording-key:LCS-RES-CR-ENABLERECORDING}")
    private String sceneEnableRecordingKey;

    @Value("${mp.cscene.scene-edit-node-limit:1000}")
    private Integer sceneEditNodeLimit;

    @Value("${mp.cscene.scene-using-node-limit:1000}")
    private Integer sceneUsingNodeLimit;

    @Value("${mp.cscene.scene-enable-recording:false}")
    private Boolean sceneEnableRecording;

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

    public int getSceneCreateNodeLimit() {
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
}
