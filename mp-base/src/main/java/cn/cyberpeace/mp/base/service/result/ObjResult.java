 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ObjResult.java.
 // File size 694.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObjResult<T> extends BaseObjResult<T, T>  {

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
