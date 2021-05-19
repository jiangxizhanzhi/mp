package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneNodeModel;
import lombok.Data;

@Data
public abstract class BaseSceneNodeBO<M extends BaseSceneNodeModel> extends EidBO<M> {
    private String id;
    private String area;
    private String icon2;
    private String position2;
    private String icon3;
    private String position3;
    private String name;
    private String description;

    @Override
    public void fromModel(M obj) {
        super.fromModel(obj);
        setId(obj.getNodeId());
    }

    @Override
    public M toModel() {
        var obj = super.toModel();
        obj.setNodeId(id);
        obj.setId(null);
        return obj;
    }
}
