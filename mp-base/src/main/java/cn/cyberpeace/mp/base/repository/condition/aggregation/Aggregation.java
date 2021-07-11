package cn.cyberpeace.mp.base.repository.condition.aggregation;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;


@Data
public abstract class Aggregation<T extends Aggregation<T>> {

    private String fieldName;

    private Function<String, String> calc;

    public static Function<String, String> genCalcEqual(Object value) {
        return column -> String.format("case when %s = %s then 1 else null end", column, value.toString());
    }

    public static Function<String, String> genCalcEmptyNull() {
        return column -> String.format("case when %s <> '' then %s else null end", column, column);
    }

    public T addCalc(Function<String, String> calc) {
        var rawCalc = this.calc;
        if (rawCalc == null) {
            this.calc = calc;
        } else {
            this.calc = column -> calc.apply(rawCalc.apply(column));
        }
        return (T) this;
    }

    public T calcEqual(Object value) {
        this.addCalc(genCalcEqual(value));
        return (T) this;
    }

    public T calcEmptyNull() {
        this.addCalc(genCalcEmptyNull());
        return (T) this;
    }

    
    public abstract String getFuncName();

    
    public String toSqlField(String columnName) {
        return StringUtils.join(getFuncName(), "(", calc == null ? columnName : calc.apply(columnName), ")");
    }

}
