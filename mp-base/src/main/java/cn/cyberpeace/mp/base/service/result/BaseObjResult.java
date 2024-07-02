package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @ClassName BaseObjResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public abstract class BaseObjResult<T, R> {
    @JsonIgnore
    private DynamicFields fields;

    /**
     * objToResult
     *
     * @param obj
     * @param fields
     * @return
     */
    public Object objToResult(T obj, DynamicFields fields) {
        if (obj == null || fields == null) {
            return obj;
        }

        return fields.apply(obj);
    }

    /**
     * objToResultWithNull
     *
     * @param obj
     * @param fields
     * @return
     */
    public T objToResultWithNull(T obj, DynamicFields fields) {
        if (obj == null || fields == null) {
            return obj;
        }

        return fields.applyWithNull(obj);
    }

    /**
     * asResult
     *
     * @return
     */
    public abstract R asResult();
}
