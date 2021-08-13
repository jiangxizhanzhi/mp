package cn.cyberpeace.mp.scene.core.plugin.bo;

import cn.cyberpeace.mp.scene.core.bo.base.ModelBO;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import lombok.Data;

@Data
public abstract class BasePluginSceneBO<M extends BasePluginSceneModel> extends ModelBO<M> {
}
