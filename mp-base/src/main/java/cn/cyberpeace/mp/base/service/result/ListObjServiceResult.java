 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ListObjServiceResult.java.
 // File size 2919.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ListObjServiceResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ServiceResult
 */
@NoArgsConstructor
public class ListObjServiceResult<T> extends ServiceResult<ListObjResult<T>> {

    /**
     * ListObjServiceResult
     *
     * @param source
     */
    public ListObjServiceResult(ListObjResult<T> source) {
        super(source);
    }

    /**
     * ListObjServiceResult
     *
     * @param source
     * @param code
     */
    public ListObjServiceResult(ListObjResult<T> source, ServiceCode code) {
        super(source, code);
    }

    /**
     * ListObjServiceResult
     *
     * @param source
     * @param code
     * @param detail
     */
    public ListObjServiceResult(ListObjResult<T> source, ServiceCode code, String detail) {
        super(source, code, detail);
    }

    /**
     * ListObjServiceResult
     *
     * @param source
     * @param total
     */
    public ListObjServiceResult(List<T> source, int total) {
        this(new ListObjResult<>(source, total));
    }

    /**
     * ListObjServiceResult
     *
     * @param source
     * @param total
     * @param fields
     */
    public ListObjServiceResult(List<T> source, int total, DynamicFields fields) {
        this(new ListObjResult<>(source, total, fields));
    }

    /**
     * sourceToData
     *
     * @param source
     * @return
     */
    @Override
    public ListObjResult<T> sourceToData(ListObjResult<T> source) {
        if (source == null) {
            return null;
        }

        source.asResult();
        return source;
    }

    /**
     * asResult
     *
     * @return
     */
    @Override
    public ListObjServiceResult<T> asResult() {
        super.asResult();
        return this;
    }

    /**
     * sourceObjs
     *
     * @return
     */
    public List<T> sourceObjs() {
        ListObjResult<T> source = getSource();
        if (source == null) {
            return new ArrayList<>();
        }
        return source.getObjs();
    }

    /**
     * sourceList
     *
     * @return
     */
    public List<T> sourceList() {
        ListObjResult<T> source = getSource();
        if (source == null) {
            return new ArrayList<>();
        }
        return source.getList();
    }

    /**
     * sourceTotal
     *
     * @return
     */
    public Integer sourceTotal() {
        ListObjResult<T> source = getSource();
        if (source == null) {
            return null;
        }
        return source.getTotal();
    }

}
