package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import lombok.Data;

/**
 * @ClassName SceneConfigElementBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementBO
 */
@Data
public class SceneConfigElementBO extends BaseSceneElementBO<SceneConfigElementModel, SceneConfigElementPortBO,
        SceneConfigElementBO> {

    @Override
    public SceneConfigElementModel createModel() {
        return new SceneConfigElementModel();
    }

}
