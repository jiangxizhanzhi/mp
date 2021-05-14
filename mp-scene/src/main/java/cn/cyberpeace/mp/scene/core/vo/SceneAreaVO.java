package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneAreaVO;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import lombok.Data;

@Data
public class SceneAreaVO extends BaseSceneAreaVO<SceneAreaModel> {

    @Override
    public SceneAreaModel createModel() {
        return new SceneAreaModel();
    }

}
