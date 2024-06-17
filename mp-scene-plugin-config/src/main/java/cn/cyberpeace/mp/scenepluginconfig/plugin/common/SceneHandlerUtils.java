package cn.cyberpeace.mp.scenepluginconfig.plugin.common;

import cn.cyberpeace.mp.scenepluginconfig.plugin.ConfigScenePluginHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class SceneHandlerUtils {

    private ConfigScenePluginHandler pluginHandler;

    public SceneHandlerUtils(ConfigScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
    }
}
