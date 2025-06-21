 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name StatCommon.java.
 // File size 15617.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Sum;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName StatCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class StatCommon {

    private StatCommon() {}

    /**
     * 分组查询指定字段的聚合count数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param groupFieldName 分组字段
     * @param countFieldNames 统计字段
     * @param distinct 是否去重
     * @param <T> 分组聚合字段的值类型
     * @return 字段的聚合count数量 Map<字段名, Map<分组字段值, 该字段值聚合结果>>
     */
    public static <T> Map<String, Map<T, Long>> groupCount(BaseModelMapper<?> mapper, HumanCondition condition,
                                                           String groupFieldName, List<String> countFieldNames,
                                                           boolean distinct) {
        List<AggStatField> countFields = new ArrayList<>();
        for (String fieldName : countFieldNames) {
            CountField f = CountField.f(fieldName, distinct);
            countFields.add(f);
        }
        GroupStatResult<T, ?> statResult = groupAggStat(mapper, condition, groupFieldName, countFields);
        return statResult.getCountResult();
    }

    /**
     * 分组查询指定字段的聚合count数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param groupFieldName 分组字段
     * @param sumFieldNames 统计字段
     * @param <T> 分组聚合字段的值类型
     * @param <S> 统计值类型
     * @return 字段的聚合count数量 Map<字段名, Map<分组字段值, 该字段值聚合结果>>
     */
    public static <T, S extends Number> Map<String, Map<T, S>> groupSum(
            BaseModelMapper<?> mapper, HumanCondition condition, String groupFieldName, List<String> sumFieldNames) {
        List<AggStatField> sumFields = new ArrayList<>();
        for (String sumFieldName : sumFieldNames) {
            SumField f = SumField.f(sumFieldName);
            sumFields.add(f);
        }
        GroupStatResult<T, S> statResult = groupAggStat(mapper, condition, groupFieldName, sumFields);
        return statResult.getSumResult();
    }

    /**
     * 分组聚合统计指定字段结果
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param groupFieldName 分组字段
     * @param statFields 统计字段
     * @param <T> 分组字段的值类型
     * @param <S> sum统计结果类型
     * @return 字段的聚合结果
     */
    public static <T, S extends Number> GroupStatResult<T, S> groupAggStat(
            BaseModelMapper<?> mapper, HumanCondition condition, String groupFieldName, List<AggStatField> statFields) {
        condition.selectAs(groupFieldName, groupFieldName).groupBy(groupFieldName);

        Map<String, Map<T, Long>> countResult = new HashMap<>();
        Map<String, Map<T, S>> sumResult = new HashMap<>();
        List<CountField> countFields = new ArrayList<>();
        List<SumField> sumFields = new ArrayList<>();
        for (AggStatField statField: statFields) {
            statField.loadToCondition(condition);

            if (statField instanceof CountField) {
                countFields.add((CountField) statField);
                countResult.put(statField.getName(), new HashMap<>());
            } else if (statField instanceof SumField) {
                sumFields.add((SumField) statField);
                sumResult.put(statField.getName(), new HashMap<>());
            }
        }

        List<Map<String, Object>> statLines = mapper.originList(condition);
        for (Map<String, Object> statLine: statLines) {
            T groupValue = (T) statLine.get(groupFieldName);
            for (CountField coundField: countFields) {
                countResult.get(coundField.getName())
                        .put(groupValue, Long.valueOf(statLine.get(coundField.getQueryKey()).toString()));
            }
            for (SumField sumField: sumFields) {
                sumResult.get(sumField.getName())
                        .put(groupValue, (S) statLine.get(sumField.getQueryKey()));
            }
        }
        GroupStatResult<T, S> statResult = new GroupStatResult<>();
        statResult.setCountResult(countResult);
        statResult.setSumResult(sumResult);
        return statResult;
    }

    /**
     * 分组查询指定字段的聚合计算结果，对于无法直接聚合统计的内容
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param groupFieldName 分组聚合字段
     * @param fieldCalcMap 统计字段对应的计算方法
     * @param <T> 分组聚合分组字段的值类型
     * @return 字段的聚合count数量 Map<字段名, Map<分组字段值, 该字段值聚合结果>>
     */
    public static <T> Map<String, Map<T, Object>> groupCalc(
            BaseModelMapper<?> mapper, HumanCondition condition, String groupFieldName,
            Map<String, Function<List<Object>, Object>> fieldCalcMap) {
        condition.selectAs(groupFieldName, groupFieldName).groupBy(groupFieldName);
        Set<String> fieldNames = fieldCalcMap.keySet();
        for (String string : fieldNames) {
            condition.selectAs(string, string);
        }

        List<Map<String, Object>> result = mapper.originList(condition);
        Map<String, Map<T, List<Object>>> fieldGroupValuesMap = new HashMap<>();
        for (Map<String, Object> resultItem : result) {
            for (String fieldName : fieldNames) {
                Map<T, List<Object>> fieldGroupValues = fieldGroupValuesMap.getOrDefault(fieldName, new HashMap<>());
                T groupValue = (T) resultItem.get(groupFieldName);
                List<Object> groupValues = fieldGroupValues.getOrDefault(groupValue, new ArrayList<>());
                Object fieldValue = resultItem.get(fieldName);
                groupValues.add(fieldValue);
                fieldGroupValues.put(groupValue, groupValues);
                fieldGroupValuesMap.put(fieldName, fieldGroupValues);
            }
        }

        Map<String, Map<T, Object>> fieldGroupCalcValueMap = new HashMap<>();
        for (String s : fieldNames) {
            fieldGroupCalcValueMap.put(s, new HashMap<>());
        }
        for (Map.Entry<String, Map<T, List<Object>>> e : fieldGroupValuesMap.entrySet()) {
            String fieldName = e.getKey();
            Map<T, List<Object>> fieldGroupValues = e.getValue();
            Map<T, Object> fieldGroupCalcValue = fieldGroupCalcValueMap.get(fieldName);
            Function<List<Object>, Object> calc = fieldCalcMap.get(fieldName);
            for (Map.Entry<T, List<Object>> entry : fieldGroupValues.entrySet()) {
                T groupValue = entry.getKey();
                List<Object> groupValues = entry.getValue();
                fieldGroupCalcValue.put(groupValue, calc.apply(groupValues));
            }
        }
        return fieldGroupCalcValueMap;
    }

    /**
     * 查询指定字段的聚合count数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param fieldNames 统计字段名称列表
     * @param distinct 是否去重
     * @return 字段的聚合count数量 Map<字段名, 该字段值统计数量>
     */
    public static Map<String, Long> count(BaseModelMapper<?> mapper, HumanCondition condition,
                                          List<String> fieldNames, boolean distinct) {
        List<AggStatField> countFields = new ArrayList<>();
        for (String fieldName : fieldNames) {
            CountField f = CountField.f(fieldName, distinct);
            countFields.add(f);
        }
        StatResult<?> statResult = aggStat(mapper, condition, countFields);
        return statResult.getCountResult();
    }

    /**
     * 查询指定字段的聚合sum数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param fieldNames 统计字段名称列表
     * @param <S> 统计值类型
     * @return 字段的聚合sum数量 Map<字段名, 该字段值统计数量>
     */
    public static <S extends Number> Map<String, S> sum(
            BaseModelMapper<?> mapper, HumanCondition condition, List<String> fieldNames) {
        List<AggStatField> sumFields = new ArrayList<>();
        for (String fieldName : fieldNames) {
            SumField f = SumField.f(fieldName);
            sumFields.add(f);
        }
        StatResult<S> statResult = aggStat(mapper, condition, sumFields);
        return statResult.getSumResult();
    }

    /**
     * 聚合统计指定字段结果
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param statFields 统计字段
     * @param <S> sum统计结果类型
     * @return 字段的聚合结果
     */
    public static <S extends Number> StatResult<S> aggStat(
            BaseModelMapper<?> mapper, HumanCondition condition, List<AggStatField> statFields) {
        Map<String, Long> countResult = new HashMap<>();
        Map<String, S> sumResult = new HashMap<>();
        List<CountField> countFields = new ArrayList<>();
        List<SumField> sumFields = new ArrayList<>();
        for (AggStatField statField: statFields) {
            statField.loadToCondition(condition);

            if (statField instanceof CountField) {
                countFields.add((CountField) statField);
                countResult.put(statField.getName(), 0L);
            } else if (statField instanceof SumField) {
                sumFields.add((SumField) statField);
                sumResult.put(statField.getName(), null);
            }
        }

        List<Map<String, Object>> statLines = mapper.originList(condition);

        for (Map<String, Object> statLine: statLines) {
            for (CountField coundField: countFields) {
                countResult.put(coundField.getName(), Long.valueOf(statLine.get(coundField.getQueryKey()).toString()));
            }
            for (SumField sumField: sumFields) {
                sumResult.put(sumField.getName(), (S) statLine.get(sumField.getQueryKey()));
            }
        }
        StatResult<S> statResult = new StatResult<>();
        statResult.setCountResult(countResult);
        statResult.setSumResult(sumResult);
        return statResult;
    }

    /**
     * 分组查询指定字段的聚合计算结果，对于无法直接聚合统计的内容
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param fieldCalcMap 统计字段对应的计算方法
     * @return 字段的聚合count数量 Map<字段名, 该字段值聚合结果>
     */
    public static Map<String, Object> calc(BaseModelMapper<?> mapper, HumanCondition condition,
                                           Map<String, Function<List<Object>, Object>> fieldCalcMap) {
        Set<String> fieldNames = fieldCalcMap.keySet();
        for (String string : fieldNames) {
            condition.selectAs(string, string);
        }
        List<Map<String, Object>> result = mapper.originList(condition);
        Map<String, List<Object>> fieldValuesMap = new HashMap<>();
        for (Map<String, Object> resultItem : result) {
            for (String fieldName : fieldNames) {
                Object fieldValue = resultItem.get(fieldName);
                List<Object> fieldValues = fieldValuesMap.getOrDefault(fieldName, new ArrayList<>());
                fieldValues.add(fieldValue);
                fieldValuesMap.put(fieldName, fieldValues);
            }
        }
        Map<String, Object> fieldCalcValueMap = new HashMap<>();
        for (String s : fieldNames) {
            fieldCalcValueMap.put(s, null);
        }
        for (Map.Entry<String, List<Object>> entry : fieldValuesMap.entrySet()) {
            String fieldName = entry.getKey();
            List<Object> fieldValues = entry.getValue();
            fieldCalcValueMap.put(fieldName, fieldCalcMap.get(fieldName).apply(fieldValues));
        }
        return fieldCalcValueMap;
    }

    @Data
    public static class GroupStatResult<T, S extends Number> {
        private Map<String, Map<T, Long>> countResult;
        private Map<String, Map<T, S>> sumResult;
    }

    @Data
    public static class StatResult<S extends Number> {
        private Map<String, Long> countResult;
        private Map<String, S> sumResult;
    }

    @Data
    public abstract static class AggStatField {
        private String name;
        private String queryKey = TextUtils.uuid();

        public abstract void loadToCondition(HumanCondition condition);
    }

    @Data
    public static class CountField extends AggStatField {
        private Boolean distinct;

        public static CountField f(String name) {
            return f(name, false);
        }

        public static CountField f(String name, boolean distinct) {
            CountField field = new CountField();
            field.setName(name);
            field.setDistinct(distinct);
            return field;
        }

        /**
         * loadToCondition
         *
         * @param condition
         */
        @Override
        public void loadToCondition(HumanCondition condition) {
            if (Boolean.TRUE.equals(getDistinct())) {
                condition = condition.selectAs(Count.f(getName()).calcEmptyNull().distinct(), getQueryKey());
            } else {
                condition = condition.selectAs(Count.f(getName()).calcEmptyNull(), getQueryKey());
            }
        }
    }

    @Data
    public static class SumField extends AggStatField {
        public static SumField f(String name) {
            SumField field = new SumField();
            field.setName(name);
            return field;
        }

        /**
         * loadToCondition
         *
         * @param condition
         */
        @Override
        public void loadToCondition(HumanCondition condition) {
            condition = condition.selectAs(Sum.f(getName()), getQueryKey());
        }
    }

}
