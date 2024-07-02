package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortConnectionModel;
import lombok.Data;

/**
 * @ClassName SceneConfigElementPortConnectionBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementPortConnectionBO
 */
@Data
public class SceneConfigElementPortConnectionBO
        extends BaseSceneElementPortConnectionBO<SceneConfigElementPortConnectionModel,
        SceneConfigElementPortConnectionBO> {

    @Override
    public SceneConfigElementPortConnectionModel createModel() {
        return new SceneConfigElementPortConnectionModel();
    }

}
