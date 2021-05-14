package cn.cyberpeace.mp.scene.core.vo.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

@Data
public abstract class EidVO<M extends BaseModel> extends ModelVO<M> {
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
