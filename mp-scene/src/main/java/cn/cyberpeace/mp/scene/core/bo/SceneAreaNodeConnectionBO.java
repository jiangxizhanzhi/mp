package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneAreaNodeConnectionModel;
import lombok.Data;

/**
 * 场景节点连接bo
 *
 * @author 何韦
 * @date 2023/05/05
 */
@Data
public class SceneAreaNodeConnectionBO
        extends BaseSceneAreaNodeConnectionBO<SceneAreaNodeConnectionModel, SceneAreaNodeConnectionBO> {

    @Override
    public SceneAreaNodeConnectionModel createModel() {
        return new SceneAreaNodeConnectionModel();
    }

    @Override
    public SceneElementPortConnectionBO convertAreaNodeConnectToConnection() {
        SceneElementPortConnectionBO sceneElementPortConnectionBO = new SceneElementPortConnectionBO();
        sceneElementPortConnectionBO.setId(this.getId());
        sceneElementPortConnectionBO.setSource(this.getSource());
        sceneElementPortConnectionBO.setTarget(this.getTarget());
        sceneElementPortConnectionBO.setEid(this.getEid());
        sceneElementPortConnectionBO.setName(this.getName());
        sceneElementPortConnectionBO.setDisplayContext(this.getDisplayContext());
        sceneElementPortConnectionBO.setVertices(this.getVertices());
        return sceneElementPortConnectionBO;
    }
}
