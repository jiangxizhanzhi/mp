package cn.cyberpeace.mp.base.repository.condition.aggregation;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Sum extends Aggregation<Sum> {

    public static Sum f(String fieldName) {
        var aggregation = new Sum();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    public static <T> Sum f(SerializableFunction<T> getter) {
        String fieldName = BeanUtils.getBeanGetterFieldName(getter);
        return f(fieldName);
    }

    @Override
    public String getFuncName() {
        return "sum";
    }

}
