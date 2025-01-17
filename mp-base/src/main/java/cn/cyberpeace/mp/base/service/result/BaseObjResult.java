 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseObjResult.java.
 // File size 723.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
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
