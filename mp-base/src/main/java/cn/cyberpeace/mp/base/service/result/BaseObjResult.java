package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public abstract class BaseObjResult<T, R> {
    @JsonIgnore
    private DynamicFields fields;

    public Object objToResult(T obj, DynamicFields fields) {
        if (obj == null || fields == null) {
            return obj;
        }

        return fields.apply(obj);
    }

    public T objToResultWithNull(T obj, DynamicFields fields) {
        if (obj == null || fields == null) {
            return obj;
        }

        return fields.applyWithNull(obj);
    }

    public abstract R asResult();
}
