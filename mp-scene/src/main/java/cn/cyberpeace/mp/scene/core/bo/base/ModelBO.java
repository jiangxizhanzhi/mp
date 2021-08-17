package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.util.BeanUtils;
import lombok.Data;

@SuppressWarnings("java:S1610")
@Data
public abstract class ModelBO<M extends BaseModel> {

    public void fromModel(M obj, String... ignoreFields) {
        BeanUtils.copyPropertiesIgnoreNull(obj, this, ignoreFields);
    }

    public abstract M createModel();

    public M toModel(String... ignoreFields) {
        var obj = createModel();
        BeanUtils.copyPropertiesIgnoreNull(this, obj, ignoreFields);
        return obj;
    }

}
