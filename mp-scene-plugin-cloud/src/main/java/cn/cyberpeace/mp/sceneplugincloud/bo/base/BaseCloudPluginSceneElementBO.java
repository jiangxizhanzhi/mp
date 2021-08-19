package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.Data;

@Data
public abstract class BaseCloudPluginSceneElementBO<M extends BaseCloudPluginSceneElementModel>
        extends BasePluginSceneElementBO<M> {
}
