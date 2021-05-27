package cn.cyberpeace.mp.base.repository.condition.aggregation;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Sum extends Aggregation {

    public static Sum f(String fieldName) {
        var aggregation = new Sum();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    @Override
    public String getFuncName() {
        return "sum";
    }

}
