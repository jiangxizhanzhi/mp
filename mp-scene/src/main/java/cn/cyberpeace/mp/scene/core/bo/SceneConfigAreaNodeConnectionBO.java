package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaNodeConnectionModel;
import lombok.Data;

/**
 * 场景模板节点连接bo
 *
 * @author 何韦
 * @date 2023/05/05
 */
@Data
public class SceneConfigAreaNodeConnectionBO
        extends BaseSceneAreaNodeConnectionBO<SceneConfigAreaNodeConnectionModel, SceneConfigAreaNodeConnectionBO> {

    @Override
    public SceneConfigAreaNodeConnectionModel createModel() {
        return new SceneConfigAreaNodeConnectionModel();
    }

    @Override
    public SceneConfigElementPortConnectionBO convertAreaNodeConnectToConnection() {
        SceneConfigElementPortConnectionBO sceneConfigElementPortConnectionBO = new SceneConfigElementPortConnectionBO();
        sceneConfigElementPortConnectionBO.setId(this.getId());
        sceneConfigElementPortConnectionBO.setSource(this.getSource());
        sceneConfigElementPortConnectionBO.setTarget(this.getTarget());
        sceneConfigElementPortConnectionBO.setEid(this.getEid());
        sceneConfigElementPortConnectionBO.setName(this.getName());
        sceneConfigElementPortConnectionBO.setDisplayContext(this.getDisplayContext());
        sceneConfigElementPortConnectionBO.setVertices(this.getVertices());
        return sceneConfigElementPortConnectionBO;    }
}
