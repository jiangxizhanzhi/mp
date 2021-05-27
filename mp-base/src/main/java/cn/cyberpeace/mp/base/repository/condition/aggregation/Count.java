package cn.cyberpeace.mp.base.repository.condition.aggregation;

public class Count extends Aggregation {

    public static Count f(String fieldName) {
        var aggregation = new Count();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    @Override
    public String getFuncName() {
        return "count";
    }

}
