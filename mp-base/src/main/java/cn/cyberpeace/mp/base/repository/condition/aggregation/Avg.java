package cn.cyberpeace.mp.base.repository.condition.aggregation;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;

public class Avg extends Aggregation<Avg> {

    public static Avg f(String fieldName) {
        var aggregation = new Avg();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    public static <T> Avg f(SerializableFunction<T> getter) {
        String fieldName = BeanUtils.getBeanGetterFieldName(getter);
        return f(fieldName);
    }

    @Override
    public String getFuncName() {
        return "avg";
    }

}
