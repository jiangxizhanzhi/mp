package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneVO;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import lombok.Data;

@Data
public class SceneConfigVO extends BaseSceneVO<SceneConfigModel> {

    @Override
    public SceneConfigModel createModel() {
        return new SceneConfigModel();
    }

}
