package cn.cyberpeace.mp.sceneplugingns3.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.base.BaseGns3PluginSceneBO;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneConfigModel;
import lombok.Data;

@Data
public class Gns3PluginSceneConfigBO
        extends BaseGns3PluginSceneBO<Gns3PluginSceneConfigModel, Gns3PluginSceneConfigBO, SceneConfigBO> {
    @Override
    public Gns3PluginSceneConfigModel createModel() {
        return new Gns3PluginSceneConfigModel();
    }
}
