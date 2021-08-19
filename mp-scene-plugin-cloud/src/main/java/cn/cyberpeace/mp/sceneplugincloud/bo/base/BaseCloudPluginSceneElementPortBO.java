package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementPortModel;
import lombok.Data;

@Data
public abstract class BaseCloudPluginSceneElementPortBO<M extends BaseCloudPluginSceneElementPortModel>
        extends BasePluginSceneElementPortBO<M> {
}
