package cn.cyberpeace.mp.base.bo;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.SerializableFunction;
import lombok.Data;

import java.util.Arrays;

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

    @SafeVarargs
    public final <T> void fromModelIgnore(M obj, SerializableFunction<T>... ignoreFieldGetters) {
        String[] ignoreFields = Arrays.stream(ignoreFieldGetters).map(BeanUtils::getBeanGetterFieldName)
                .toArray(String[]::new);
        fromModel(obj, ignoreFields);
    }

    @SafeVarargs
    public final <T> M toModelIgnore(SerializableFunction<T>... ignoreFieldGetters) {
        String[] ignoreFields = Arrays.stream(ignoreFieldGetters).map(BeanUtils::getBeanGetterFieldName)
                .toArray(String[]::new);
        return toModel(ignoreFields);
    }
}
