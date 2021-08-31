package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortModel;
import lombok.Data;

@Data
public class SceneConfigElementPortBO
        extends BaseSceneElementPortBO<SceneConfigElementPortModel, SceneConfigElementPortBO> {

    @Override
    public SceneConfigElementPortModel createModel() {
        return new SceneConfigElementPortModel();
    }

}
