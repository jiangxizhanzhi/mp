package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortConnectionModel;
import lombok.Data;

@Data
public abstract class BaseSceneElementPortConnectionBO<M extends BaseSceneElementPortConnectionModel> extends EidBO<M> {
    private String source;
    private String target;
    private String name;
    private Boolean unidirectional;

    @Override
    public void fromModel(M obj) {
        super.fromModel(obj);
        setSource(obj.getSourcePortId());
        setTarget(obj.getTargetPortId());
    }

    @Override
    public M toModel() {
        var obj = super.toModel();
        obj.setSourcePortId(source);
        obj.setTargetPortId(target);
        return obj;
    }
}
