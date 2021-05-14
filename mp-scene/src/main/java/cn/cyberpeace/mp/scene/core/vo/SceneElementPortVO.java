package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneElementPortVO;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import lombok.Data;

@Data
public class SceneElementPortVO extends BaseSceneElementPortVO<SceneElementPortModel> {

    @Override
    public SceneElementPortModel createModel() {
        return new SceneElementPortModel();
    }

}
