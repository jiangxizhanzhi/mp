 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ObjServiceResult.java.
 // File size 1302.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ObjServiceResult<T> extends BaseServiceResult<ObjResult<T>, T> {

    public ObjServiceResult(ObjResult<T> source) {
        super(source);
    }

    public ObjServiceResult(ObjResult<T> source, ServiceCode code) {
        super(source, code);
    }

    public ObjServiceResult(ObjResult<T> source, ServiceCode code, String detail) {
        super(source, code, detail);
    }

    public ObjServiceResult(T obj) {
        this(new ObjResult<>(obj));
    }

    public ObjServiceResult(T obj, DynamicFields fields) {
        this(new ObjResult<>(obj, fields));
    }

    @Override
    public T sourceToData(ObjResult<T> source) {
        if (source == null) {
            return null;
        }

        return source.asResult();
    }

    @Override
    public ObjServiceResult<T> asResult() {
        super.asResult();
        return this;
    }

    public T sourceObj() {
        var source = getSource();
        if (source == null) {
            return null;
        }
        return source.getObj();
    }

}
