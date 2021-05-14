package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneElementPortVO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortModel;
import lombok.Data;

@Data
public class SceneConfigElementPortVO extends BaseSceneElementPortVO<SceneConfigElementPortModel> {

    @Override
    public SceneConfigElementPortModel createModel() {
        return new SceneConfigElementPortModel();
    }

}
