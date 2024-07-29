package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneNodeConnectionModel;
import lombok.Data;

/**
 * 场景节点连接bo
 * @author 何韦
 * @date 2023/05/05
 */
@Data
public class SceneAreaNodeConnectionBO extends BaseSceneAreaNodeConnectionBO<SceneNodeConnectionModel, SceneAreaNodeConnectionBO> {

    @Override
    public SceneNodeConnectionModel createModel() {
        return new SceneNodeConnectionModel();
    }

}
