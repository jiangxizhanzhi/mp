 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Min.java.
 // File size 712.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.repository.condition.aggregation;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Min extends Aggregation<Min> {

    public static Min f(String fieldName) {
        var aggregation = new Min();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    public static <T> Min f(SerializableFunction<T> getter) {
        String fieldName = BeanUtils.getBeanGetterFieldName(getter);
        return f(fieldName);
    }

    @Override
    public String getFuncName() {
        return "min";
    }

}
