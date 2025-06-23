 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Min.java.
 // File size 1051.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.repository.condition.aggregation;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import lombok.AllArgsConstructor;

/**
 * @ClassName Min
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Aggregation
 */
@AllArgsConstructor
public class Min extends Aggregation<Min> {

    /**
     * f
     *
     * @param fieldName
     * @return
     */
    public static Min f(String fieldName) {
        Min aggregation = new Min();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    /**
     * f
     *
     * @param getter
     * @return
     */
    public static <T> Min f(SerializableFunction<T> getter) {
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
        return "min";
    }

}
