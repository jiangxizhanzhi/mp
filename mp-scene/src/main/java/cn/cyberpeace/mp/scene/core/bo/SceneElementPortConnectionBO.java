package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import lombok.Data;

/**
 * @ClassName SceneElementPortConnectionBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementPortConnectionBO
 */
@Data
public class SceneElementPortConnectionBO
        extends BaseSceneElementPortConnectionBO<SceneElementPortConnectionModel, SceneElementPortConnectionBO> {

    @Override
    public SceneElementPortConnectionModel createModel() {
        return new SceneElementPortConnectionModel();
    }

}
