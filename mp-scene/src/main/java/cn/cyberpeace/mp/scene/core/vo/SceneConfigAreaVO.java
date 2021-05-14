package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneAreaVO;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaModel;
import lombok.Data;

@Data
public class SceneConfigAreaVO extends BaseSceneAreaVO<SceneConfigAreaModel> {

    @Override
    public SceneConfigAreaModel createModel() {
        return new SceneConfigAreaModel();
    }

}
