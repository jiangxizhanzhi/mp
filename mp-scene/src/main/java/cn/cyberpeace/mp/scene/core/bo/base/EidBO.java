package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.base.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public abstract class EidBO<M extends BaseModel, T extends EidBO<M, T>> extends ModelBO<M, T> {
    @ApiModelProperty(value = "服务端id")
    private String eid;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ignoreFields);
        eid = obj.getId();
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ignoreFields);
        obj.setId(eid);
        return obj;
    }

    @Override
    public T export() {
        setEid(null);
        return (T) this;
    }

}
