package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaNodeConnectionModel;
import lombok.Data;

/**
 * @ClassName SceneConfigAreaNodeConnectionBO
 * @Description 场景模板节点连接bo
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneAreaNodeConnectionBO
 */
@Data
public class SceneConfigAreaNodeConnectionBO
        extends BaseSceneAreaNodeConnectionBO<SceneConfigAreaNodeConnectionModel, SceneConfigAreaNodeConnectionBO> {

    @Override
    public SceneConfigAreaNodeConnectionModel createModel() {
        return new SceneConfigAreaNodeConnectionModel();
    }

}
