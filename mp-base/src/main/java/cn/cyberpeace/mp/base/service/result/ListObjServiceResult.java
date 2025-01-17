 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ListObjServiceResult.java.
 // File size 1896.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ListObjServiceResult<T> extends ServiceResult<ListObjResult<T>> {

    public ListObjServiceResult(ListObjResult<T> source) {
        super(source);
    }

    public ListObjServiceResult(ListObjResult<T> source, ServiceCode code) {
        super(source, code);
    }

    public ListObjServiceResult(ListObjResult<T> source, ServiceCode code, String detail) {
        super(source, code, detail);
    }

    public ListObjServiceResult(List<T> source, int total) {
        this(new ListObjResult<>(source, total));
    }

    public ListObjServiceResult(List<T> source, int total, DynamicFields fields) {
        this(new ListObjResult<>(source, total, fields));
    }

    @Override
    public ListObjResult<T> sourceToData(ListObjResult<T> source) {
        if (source == null) {
            return null;
        }

        source.asResult();
        return source;
    }

    @Override
    public ListObjServiceResult<T> asResult() {
        super.asResult();
        return this;
    }

    public List<T> sourceObjs() {
        var source = getSource();
        if (source == null) {
            return new ArrayList<>();
        }
        return source.getObjs();
    }

    public List<T> sourceList() {
        var source = getSource();
        if (source == null) {
            return new ArrayList<>();
        }
        return source.getList();
    }

    public Integer sourceTotal() {
        var source = getSource();
        if (source == null) {
            return null;
        }
        return source.getTotal();
    }

}
