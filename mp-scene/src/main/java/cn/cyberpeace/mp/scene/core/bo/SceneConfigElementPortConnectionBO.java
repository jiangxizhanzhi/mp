package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortConnectionModel;
import lombok.Data;

@Data
public class SceneConfigElementPortConnectionBO
        extends BaseSceneElementPortConnectionBO<SceneConfigElementPortConnectionModel> {

    @Override
    public SceneConfigElementPortConnectionModel createModel() {
        return new SceneConfigElementPortConnectionModel();
    }

}
