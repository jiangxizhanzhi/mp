package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneElementPortConnectionVO;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import lombok.Data;

@Data
public class SceneElementPortConnectionVO
        extends BaseSceneElementPortConnectionVO<SceneElementPortConnectionModel> {

    @Override
    public SceneElementPortConnectionModel createModel() {
        return new SceneElementPortConnectionModel();
    }

}
