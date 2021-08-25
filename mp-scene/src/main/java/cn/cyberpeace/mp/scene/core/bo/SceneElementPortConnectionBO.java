package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import lombok.Data;

@Data
public class SceneElementPortConnectionBO
        extends BaseSceneElementPortConnectionBO<SceneElementPortConnectionModel, SceneElementPortConnectionBO> {

    @Override
    public SceneElementPortConnectionModel createModel() {
        return new SceneElementPortConnectionModel();
    }

}
