package cn.cyberpeace.mp.base.repository.condition.aggregation;

public class Avg extends Aggregation {

    public static Avg f(String fieldName) {
        var aggregation = new Avg();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    @Override
    public String getFuncName() {
        return "avg";
    }

}
