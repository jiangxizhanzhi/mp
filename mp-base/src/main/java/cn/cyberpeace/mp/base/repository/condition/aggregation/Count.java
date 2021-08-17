package cn.cyberpeace.mp.base.repository.condition.aggregation;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.SerializableFunction;

public class Count extends Aggregation {

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
