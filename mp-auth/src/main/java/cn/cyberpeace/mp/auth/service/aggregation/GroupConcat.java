package cn.cyberpeace.mp.auth.service.aggregation;

import cn.cyberpeace.mp.base.repository.condition.aggregation.Aggregation;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;

import java.util.function.Function;

/**
 * @ClassName GroupConcat
 * @Description
 * @Author shgang
 * @Date 2022/11/2
 * @Version 1.0.0
 * @see Aggregation
 */
public class GroupConcat extends Aggregation<GroupConcat> {
    public static Function<String, String> genDistinct() {
        return column -> String.format("distinct (%s)", column);
    }

    /**
     * distinct
     *
     * @return
     */
    public GroupConcat distinct() {
        addCalc(genDistinct());
        return this;
    }

    /**
     * f
     *
     * @param fieldName
     * @return
     */
    public static GroupConcat f(String fieldName) {
        GroupConcat aggregation = new GroupConcat();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    /**
     * f
     *
     * @param getter
     * @return
     */
    public static <T> GroupConcat f(SerializableFunction<T> getter) {
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
        return "group_concat";
    }
}
