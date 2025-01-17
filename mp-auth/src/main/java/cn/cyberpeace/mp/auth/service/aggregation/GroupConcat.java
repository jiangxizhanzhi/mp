 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name GroupConcat.java.
 // File size 1105.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service.aggregation;

import cn.cyberpeace.mp.base.repository.condition.aggregation.Aggregation;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;

import java.util.function.Function;

/**
 * @Description
 * @Author shgang
 * @Date 2022/11/2
 */
public class GroupConcat extends Aggregation<GroupConcat> {
    public static Function<String, String> genDistinct() {
        return column -> String.format("distinct (%s)", column);
    }

    public GroupConcat distinct() {
        addCalc(genDistinct());
        return this;
    }

    public static GroupConcat f(String fieldName) {
        var aggregation = new GroupConcat();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    public static <T> GroupConcat f(SerializableFunction<T> getter) {
        String fieldName = BeanUtils.getBeanGetterFieldName(getter);
        return f(fieldName);
    }

    @Override
    public String getFuncName() {
        return "group_concat";
    }
}
