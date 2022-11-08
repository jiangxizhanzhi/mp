package cn.cyberpeace.mp.sceneplugingns3.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.base.BaseGns3PluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneConfigElementModel;
import lombok.Data;

@Data
public class Gns3PluginSceneConfigElementBO extends BaseGns3PluginSceneElementBO<Gns3PluginSceneConfigElementModel,
        Gns3PluginSceneConfigElementBO, SceneConfigElementBO> {
    @Override
    public Gns3PluginSceneConfigElementModel createModel() {
        return new Gns3PluginSceneConfigElementModel();
    }
}
