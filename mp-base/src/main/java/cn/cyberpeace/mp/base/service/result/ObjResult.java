package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObjResult<T> extends BaseObjResult<T, T> {

    @JsonIgnore
    private T obj;

    public ObjResult(T obj) {
        setObj(obj);
    }

    public ObjResult(T obj, DynamicFields fields) {
        setObj(obj);
        setFields(fields);
    }

    @Override
    public T asResult() {
        if (obj == null) {
            return null;
        }

        return objToResultWithNull(obj, getFields());
    }
}
