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
