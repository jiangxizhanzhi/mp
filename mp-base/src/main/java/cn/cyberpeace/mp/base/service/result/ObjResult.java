package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.model.DynamicFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObjResult<T> extends BaseResult {

    @JsonIgnore
    private T obj = null;

    public ObjResult(T obj) {
        setObj(obj);
    }

    public ObjResult(T obj, DynamicFields fields) {
        setObj(obj);
        setFields(fields);
    }

    public Object asResult() {
        if (obj == null) {
            return null;
        }

        return objToResult(obj, getFields());
    }
}
