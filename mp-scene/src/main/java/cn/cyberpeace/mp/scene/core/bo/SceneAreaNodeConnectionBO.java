package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneAreaNodeConnectionModel;
import lombok.Data;

/**
 * @ClassName SceneAreaNodeConnectionBO
 * @Description 场景节点连接bo
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneAreaNodeConnectionBO
 */
@Data
public class SceneAreaNodeConnectionBO
        extends BaseSceneAreaNodeConnectionBO<SceneAreaNodeConnectionModel, SceneAreaNodeConnectionBO> {

    @Override
    public SceneAreaNodeConnectionModel createModel() {
        return new SceneAreaNodeConnectionModel();
    }

}
