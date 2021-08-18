package cn.cyberpeace.mp.scene.core.plugin.bo;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import lombok.Data;

@Data
public abstract class BasePluginSceneElementBO<M extends BasePluginSceneElementModel> extends ModelBO<M> {
}
