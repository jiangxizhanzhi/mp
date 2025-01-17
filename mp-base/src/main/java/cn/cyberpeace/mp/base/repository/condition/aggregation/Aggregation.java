 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Aggregation.java.
 // File size 1666.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.repository.condition.aggregation;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * 标记聚合函数
 * 约定：子类通过静态方法f载入聚合字段
 */
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
