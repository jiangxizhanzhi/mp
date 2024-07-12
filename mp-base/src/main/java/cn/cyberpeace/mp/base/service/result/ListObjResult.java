 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ListObjResult.java.
 // File size 1704.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName ListObjResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseObjResult
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListObjResult<T> extends BaseObjResult<T, ListObjResult<T>> {

    @ApiModelProperty(value = "数据列表")
    private List<T> list = new ArrayList<>();

    @ApiModelProperty(value = "总数")
    private Integer total = 0;

    @JsonIgnore
    private List<T> objs = new ArrayList<>();

    /**
     * ListObjResult
     *
     * @param objs
     * @param total
     */
    public ListObjResult(List<T> objs, int total) {
        this();
        setObjs(objs);
        setTotal(total);
    }

    /**
     * ListObjResult
     *
     * @param objs
     * @param total
     * @param fields
     */
    public ListObjResult(List<T> objs, Integer total, DynamicFields fields) {
        this();
        setObjs(objs);
        setTotal(Objects.nonNull(total) ? total : 0);
        setFields(fields);
    }

    /**
     * asResult
     *
     * @return
     */
    @Override
    public ListObjResult<T> asResult() {
        list.clear();
        for (T obj : objs) {
            list.add(objToResultWithNull(obj, getFields()));
        }
        return this;
    }
}
