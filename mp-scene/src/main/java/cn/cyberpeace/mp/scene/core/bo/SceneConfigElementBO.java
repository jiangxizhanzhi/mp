package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import lombok.Data;

import java.util.List;

@Data
public class SceneConfigElementBO extends BaseSceneElementBO<SceneConfigElementModel> {

    private List<SceneConfigElementPortBO> ports;

    @Override
    public SceneConfigElementModel createModel() {
        return new SceneConfigElementModel();
    }

}
