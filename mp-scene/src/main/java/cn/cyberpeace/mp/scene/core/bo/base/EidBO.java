package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

@Data
public abstract class EidBO<M extends BaseModel> extends ModelBO<M> {
    private String eid;

    @Override
    public void fromModel(M obj) {
        super.fromModel(obj);
        setEid(obj.getId());
    }

    @Override
    public M toModel() {
        var obj = super.toModel();
        obj.setId(eid);
        return obj;
    }
}
