package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneElementVO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import lombok.Data;

@Data
public class SceneConfigElementVO extends BaseSceneElementVO<SceneConfigElementModel> {

    @Override
    public SceneConfigElementModel createModel() {
        return new SceneConfigElementModel();
    }

}
