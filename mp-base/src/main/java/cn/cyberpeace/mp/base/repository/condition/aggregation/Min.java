package cn.cyberpeace.mp.base.repository.condition.aggregation;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Min extends Aggregation {

    public static Min f(String fieldName) {
        var aggregation = new Min();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    @Override
    public String getFuncName() {
        return "min";
    }

}
