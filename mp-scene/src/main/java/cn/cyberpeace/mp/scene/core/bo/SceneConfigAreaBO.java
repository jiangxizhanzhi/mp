package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaBO;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaModel;
import lombok.Data;

/**
 * @ClassName SceneConfigAreaBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneAreaBO
 */
@Data
public class SceneConfigAreaBO extends BaseSceneAreaBO<SceneConfigAreaModel, SceneConfigAreaBO> {

    @Override
    public SceneConfigAreaModel createModel() {
        return new SceneConfigAreaModel();
    }

}
