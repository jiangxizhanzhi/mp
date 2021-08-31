package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneNodeModel;
import lombok.Data;

@Data
public abstract class BaseSceneNodeBO<M extends BaseSceneNodeModel, T extends BaseSceneNodeBO<M, T>>
        extends EidBO<M, T> {
    private String id;
    private String area;
    private String icon2;
    private String position2;
    private String icon3;
    private String position3;
    private String name;
    private String description;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ignoreFields);
        id = obj.getNodeId();
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ignoreFields);
        obj.setNodeId(id);
        obj.setId(null);
        return obj;
    }
}
