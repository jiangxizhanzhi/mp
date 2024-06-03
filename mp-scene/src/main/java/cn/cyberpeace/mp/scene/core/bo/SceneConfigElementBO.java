package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import lombok.Data;

@Data
public class SceneConfigElementBO extends BaseSceneElementBO<SceneConfigElementModel, SceneConfigElementPortBO,
        SceneConfigElementBO> {

    private String controlType;
    private String defaultCmd;

    @Override
    public SceneConfigElementModel createModel() {
        return new SceneConfigElementModel();
    }

}
