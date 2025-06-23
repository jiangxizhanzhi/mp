 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ObjResult.java.
 // File size 1035.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ObjResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseObjResult
 */
@Data
@NoArgsConstructor
public class ObjResult<T> extends BaseObjResult<T, T>  {

    @JsonIgnore
    private T obj;

    /**
     * ObjResult
     *
     * @param obj
     */
    public ObjResult(T obj) {
        setObj(obj);
    }

    /**
     * ObjResult
     *
     * @param obj
     * @param fields
     */
    public ObjResult(T obj, DynamicFields fields) {
        setObj(obj);
        setFields(fields);
    }

    /**
     * asResult
     *
     * @return
     */
    @Override
    public T asResult() {
        if (obj == null) {
            return null;
        }
        return objToResultWithNull(obj, getFields());
    }
}
