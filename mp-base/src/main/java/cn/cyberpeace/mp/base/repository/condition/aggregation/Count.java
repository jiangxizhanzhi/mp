 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Count.java.
 // File size 1401.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.repository.condition.aggregation;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;

import java.util.function.Function;

/**
 * @ClassName Count
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Aggregation
 */
public class Count extends Aggregation<Count> {

    /**
     * genDistinct
     *
     * @return
     */
    public static Function<String, String> genDistinct() {
        return column -> String.format("distinct (%s)", column);
    }

    /**
     * distinct
     *
     * @return
     */
    public Count distinct() {
        addCalc(genDistinct());
        return this;
    }

    /**
     * f
     *
     * @param fieldName
     * @return
     */
    public static Count f(String fieldName) {
        Count aggregation = new Count();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    /**
     * f
     *
     * @param getter
     * @return
     */
    public static <T> Count f(SerializableFunction<T> getter) {
        String fieldName = BeanUtils.getBeanGetterFieldName(getter);
        return f(fieldName);
    }

    /**
     * getFuncName
     *
     * @return
     */
    @Override
    public String getFuncName() {
        return "count";
    }

}
