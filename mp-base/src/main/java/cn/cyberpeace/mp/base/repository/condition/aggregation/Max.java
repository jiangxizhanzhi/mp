package cn.cyberpeace.mp.base.repository.condition.aggregation;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;

public class Max extends Aggregation<Max> {

    public static Max f(String fieldName) {
        var aggregation = new Max();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    public static <T> Max f(SerializableFunction<T> getter) {
        String fieldName = BeanUtils.getBeanGetterFieldName(getter);
        return f(fieldName);
    }

    @Override
    public String getFuncName() {
        return "max";
    }

}
