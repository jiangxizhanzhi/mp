package cn.cyberpeace.mp.scene.core.vo.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.util.BeanUtils;
import lombok.Data;

@SuppressWarnings("java:S1610")
@Data
public abstract class ModelVO<M extends BaseModel> {

    public void fromModel(M obj) {
        BeanUtils.copyPropertiesIgnoreNull(obj, this);
    }

    public abstract M createModel();

    public M toModel() {
        var obj = createModel();
        BeanUtils.copyPropertiesIgnoreNull(this, obj);
        return obj;
    }

}
