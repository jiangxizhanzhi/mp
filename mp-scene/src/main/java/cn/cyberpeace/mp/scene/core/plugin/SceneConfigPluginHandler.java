package cn.cyberpeace.mp.scene.core.plugin;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.common.BasePluginCommonHandler;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import lombok.Data;


@Data
public abstract class SceneConfigPluginHandler<S extends BasePluginSceneBO<?, S, SceneConfigBO>,
        E extends BasePluginSceneElementBO<?, E, SceneConfigElementBO>,
        P extends BasePluginSceneElementPortBO<?, P, SceneConfigElementPortBO>,
        C> extends BasePluginCommonHandler<SceneConfigBO, SceneConfigElementBO, SceneConfigElementPortBO, S, E, P, C,
        SceneConfigHandler> {

    private SceneConfigHandler sceneHandler;

}
