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
