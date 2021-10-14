package cn.cyberpeace.mp.base.repository.condition.aggregation;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * 标记聚合函数
 * 约定：子类通过静态方法f载入聚合字段
 */
@Data
public abstract class Aggregation {

    private String fieldName;

    private Function<String, String> calc;

    public static Function<String, String> genCalcEqual(Object value) {
        return column -> String.format("case when %s = %s then 1 else null end", column, value.toString());
    }

    public Aggregation calcEqual(Object value) {
        this.calc = genCalcEqual(value);
        return this;
    }

    /** 聚合函数sql名称 */
    public abstract String getFuncName();

    /**
     * 构建sql聚合字段
     * @param columnName 列名称
     * @return sql聚合字段
     */
    public String toSqlField(String columnName) {
        return StringUtils.join(getFuncName(), "(", calc == null ? columnName : calc.apply(columnName), ")");
    }

}
