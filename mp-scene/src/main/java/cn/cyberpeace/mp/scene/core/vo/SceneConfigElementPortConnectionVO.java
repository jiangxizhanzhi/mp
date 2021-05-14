package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneElementPortConnectionVO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortConnectionModel;
import lombok.Data;

@Data
public class SceneConfigElementPortConnectionVO
        extends BaseSceneElementPortConnectionVO<SceneConfigElementPortConnectionModel> {

    @Override
    public SceneConfigElementPortConnectionModel createModel() {
        return new SceneConfigElementPortConnectionModel();
    }

}
