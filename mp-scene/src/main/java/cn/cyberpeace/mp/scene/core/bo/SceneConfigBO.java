package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import lombok.Data;

/**
 * @ClassName SceneConfigBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneBO
 */
@Data
public class SceneConfigBO extends BaseSceneBO<SceneConfigModel, SceneConfigBO> {

    @Override
    public SceneConfigModel createModel() {
        return new SceneConfigModel();
    }

}
