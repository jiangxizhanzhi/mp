package cn.cyberpeace.mp.base.repository.condition.aggregation;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Min extends Aggregation {

    public static Min f(String fieldName) {
        var aggregation = new Min();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    public static <T> Min f(SerializableFunction<T> getter) {
        String fieldName = BeanUtils.getBeanGetterFieldName(getter);
        return f(fieldName);
    }

    @Override
    public String getFuncName() {
        return "min";
    }

}
