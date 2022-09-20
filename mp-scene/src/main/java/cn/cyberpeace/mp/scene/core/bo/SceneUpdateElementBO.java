package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import lombok.Data;

@Data
public class SceneUpdateElementBO extends BaseSceneElementBO<SceneElementModel, SceneUpdateElementPortBO,
        SceneUpdateElementBO> {

    @Override
    public SceneElementModel createModel() {
        return new SceneElementModel();
    }

    @Override
    public SceneUpdateElementBO export() {
        String eid = this.getEid();
        var result = super.export();
        result.setEid(eid);
        return result;
    }

    @Override
    public <P2 extends BaseSceneElementPortBO<?, P2, P1>, P1 extends BaseSceneElementBO<?, P2, P1>> P1 export(Class<P1> pClass, Class<P2> portClass, SerializableFunction<SceneUpdateElementBO>... ignoreFieldGetters) {
        String eid = this.getEid();
        var result = super.export(pClass, portClass, ignoreFieldGetters);
        result.setEid(eid);
        return result;
    }
}
