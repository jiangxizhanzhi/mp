package cn.cyberpeace.mp.sceneplugingns3.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.base.BaseGns3PluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneElementPortModel;
import lombok.Data;

@Data
public class Gns3PluginSceneElementPortBO extends BaseGns3PluginSceneElementPortBO<Gns3PluginSceneElementPortModel,
        Gns3PluginSceneElementPortBO, SceneElementPortBO> {
    @Override
    public Gns3PluginSceneElementPortModel createModel() {
        return new Gns3PluginSceneElementPortModel();
    }
}
