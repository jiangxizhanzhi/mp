package cn.cyberpeace.mp.cscene.config;

import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.front.admin.service.sys.SysConfigService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
    private SysConfigService sysConfigService;

    @Value("${mp.cscene.scene-edit-node-limit-key:MP_SCENE_EDIT_NODE_LIMIT}")
    private String sceneEditNodeLimitKey;

    @Value("${mp.cscene.scene-using-node-limit-key:MP_SCENE_USING_NODE_LIMIT}")
    private String sceneUsingNodeLimitKey;

    @Value("${mp.cscene.scene-edit-node-limit:1000}")
    private Integer sceneEditNodeLimit;

    @Value("${mp.cscene.scene-using-node-limit:1000}")
    private Integer sceneUsingNodeLimit;

    public int getSceneEditNodeLimit() {
        var config = sysConfigService.getByKeyName(sceneEditNodeLimitKey);
        if (config == null) {
            return sceneEditNodeLimit;
        }

        var value = config.getValue();
        var limit = IntUtils.fromStr(value);
        if (limit.equals(0)) {
            log.warn("SceneEditNodeLimit({}) parse as 0", value);
        }
        return limit;
    }

    public int getSceneCreateNodeLimit() {
        var config = sysConfigService.getByKeyName(sceneUsingNodeLimitKey);
        if (config == null) {
            return sceneUsingNodeLimit;
        }

        var value = config.getValue();
        var limit = IntUtils.fromStr(value);
        if (limit.equals(0)) {
            log.warn("SceneUsingNodeLimit({}) parse as 0", value);
        }
        return limit;
    }
}
