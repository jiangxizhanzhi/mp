package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaBO;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import lombok.Data;

/**
 * @ClassName SceneAreaBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneAreaBO
 */
@Data
public class SceneAreaBO extends BaseSceneAreaBO<SceneAreaModel, SceneAreaBO> {

    @Override
    public SceneAreaModel createModel() {
        return new SceneAreaModel();
    }

}
