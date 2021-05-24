package cn.cyberpeace.mp.base.repository.condition.aggregation;

public class Max extends Aggregation {

    public static Max f(String fieldName) {
        var aggregation = new Max();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    @Override
    public String getFuncName() {
        return "max";
    }

}
