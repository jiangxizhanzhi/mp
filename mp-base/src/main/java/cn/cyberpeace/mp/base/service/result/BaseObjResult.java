package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public abstract class BaseObjResult {
    @JsonIgnore
    private DynamicFields fields;

    public Object objToResult(Object obj, DynamicFields fields) {
        if (obj == null || fields == null) {
            return obj;
        }

        return fields.apply(obj);
    }

    public abstract Object asResult();
}
