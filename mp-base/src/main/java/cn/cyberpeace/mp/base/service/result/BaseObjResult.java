 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseObjResult.java.
 // File size 1111.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
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
