package cn.cyberpeace.mp.sceneplugingns3.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.base.BaseGns3PluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneConfigElementPortModel;
import lombok.Data;

@Data
public class Gns3PluginSceneConfigElementPortBO
        extends BaseGns3PluginSceneElementPortBO<Gns3PluginSceneConfigElementPortModel,
        Gns3PluginSceneConfigElementPortBO, SceneConfigElementPortBO> {
    @Override
    public Gns3PluginSceneConfigElementPortModel createModel() {
        return new Gns3PluginSceneConfigElementPortModel();
    }
}
