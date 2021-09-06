package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import lombok.Data;

@Data
public class SceneConfigBO extends BaseSceneBO<SceneConfigModel, SceneConfigBO> {

    @Override
    public SceneConfigModel createModel() {
        return new SceneConfigModel();
    }

}
