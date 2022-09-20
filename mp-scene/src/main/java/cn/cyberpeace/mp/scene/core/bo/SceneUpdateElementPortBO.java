package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import lombok.Data;

@Data
public class SceneUpdateElementPortBO
        extends BaseSceneElementPortBO<SceneElementPortModel, SceneUpdateElementPortBO, SceneUpdateElementBO> {

    @Override
    public SceneElementPortModel createModel() {
        return new SceneElementPortModel();
    }

    @Override
    public SceneUpdateElementPortBO export() {
        String eid = this.getEid();
        var result = super.export();
        result.setEid(eid);
        return result;
    }
}
