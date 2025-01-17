 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Count.java.
 // File size 937.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.repository.condition.aggregation;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;

import java.util.function.Function;

public class Count extends Aggregation<Count> {

    public static Function<String, String> genDistinct() {
        return column -> String.format("distinct (%s)", column);
    }

    public Count distinct() {
        addCalc(genDistinct());
        return this;
    }

    public static Count f(String fieldName) {
        var aggregation = new Count();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    public static <T> Count f(SerializableFunction<T> getter) {
        String fieldName = BeanUtils.getBeanGetterFieldName(getter);
        return f(fieldName);
    }

    @Override
    public String getFuncName() {
        return "count";
    }

}
