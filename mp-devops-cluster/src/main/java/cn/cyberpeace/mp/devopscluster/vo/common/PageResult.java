 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PageResult.java.
 // File size 930.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo.common;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName PageResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ListObjServiceResult
 */
@NoArgsConstructor
public class PageResult<T> extends ListObjServiceResult<T> {

    public PageResult(List<T> list, int total) {
        super(new ListObjResult<>(list,total));
        this.asResult();
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
    public PageResult<T> asResult() {
        super.asResult();
        return this;
    }
}
