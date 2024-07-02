package cn.cyberpeace.mp.base.repository.condition.aggregation;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;

public class Max extends Aggregation<Max> {

    /**
     * f
     *
     * @param fieldName
     * @return
     */
    public static Max f(String fieldName) {
        Max aggregation = new Max();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    /**
     * f
     *
     * @param getter
     * @return
     */
    public static <T> Max f(SerializableFunction<T> getter) {
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
        return "max";
    }

}
