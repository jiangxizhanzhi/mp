package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneModel;
import lombok.Data;

@Data
public abstract class BaseCloudPluginSceneBO<M extends BaseCloudPluginSceneModel,
        T extends BaseCloudPluginSceneBO<M, T>> extends BasePluginSceneBO<M, T> {
}
