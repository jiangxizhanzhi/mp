package cn.cyberpeace.mp.auth.service.aggregation;

import cn.cyberpeace.mp.base.repository.condition.aggregation.Aggregation;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;

import java.util.function.Function;


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
