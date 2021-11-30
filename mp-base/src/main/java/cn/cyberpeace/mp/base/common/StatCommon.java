package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.bo.GroupCalcItemBO;
import cn.cyberpeace.mp.base.bo.GroupSizeBO;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Sum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StatCommon {

    private StatCommon() {}

    /**
     * 分组查询指定字段的聚合count数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param groupFieldName 分组聚合字段
     * @param <T> 分组聚合字段的值类型
     * @return 字段的聚合count数量 Map<字段值, 该字段值统计数量>
     */
    public static <T> Map<T, Long> groupCount(BaseModelMapper<?> mapper, HumanCondition condition,
                                              String groupFieldName) {
        return groupCount(mapper, condition, groupFieldName, groupFieldName, false);
    }

    /**
     * 分组查询指定字段的聚合count数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param groupFieldName 分组聚合字段
     * @param countFieldName 统计字段
     * @param <T> 分组聚合字段的值类型
     * @return 字段的聚合count数量 Map<字段值, 该字段值统计数量>
     */
    public static <T> Map<T, Long> groupCount(BaseModelMapper<?> mapper, HumanCondition condition,
                                              String groupFieldName, String countFieldName, boolean distinct) {
        condition.selectAs(groupFieldName, GroupSizeBO<T, Long>::getTarget).groupBy(groupFieldName);
        if (distinct) {
            condition.selectAs(Count.f(countFieldName).distinct(), GroupSizeBO<T, Long>::getSize);
        } else {
            condition.selectAs(Count.f(countFieldName), GroupSizeBO<T, Long>::getSize);
        }
        var groupCountBOs = mapper.list(condition, new GroupSizeBO<T, Long>().getClass());
        return groupCountBOs.stream()
                .collect(Collectors.toMap(groupCountBO -> (T) groupCountBO.getTarget(), GroupSizeBO<T, Long>::getSize));
    }

    /**
     * 分组查询指定字段的聚合count数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param groupFieldName 分组聚合字段
     * @param sumFieldName 统计字段
     * @param <T> 分组聚合字段的值类型
     * @param <S> 统计值类型
     * @return 字段的聚合count数量 Map<字段值, 该字段值统计数量>
     */
    public static <T, S extends Number> Map<T, S> groupSum(
            BaseModelMapper<?> mapper, HumanCondition condition, String groupFieldName, String sumFieldName) {
        condition.selectAs(groupFieldName, GroupSizeBO<T, S>::getTarget).groupBy(groupFieldName);
        condition.selectAs(Sum.f(sumFieldName), GroupSizeBO<T, S>::getSize);
        var groupCountBOs = mapper.list(condition, new GroupSizeBO<T, S>().getClass());
        return groupCountBOs.stream()
                .collect(Collectors.toMap(groupCountBO -> (T) groupCountBO.getTarget(), GroupSizeBO<T, S>::getSize));
    }

    /**
     * 分组查询指定字段的聚合计算结果，对于无法直接聚合统计的内容
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param groupFieldName 分组聚合字段
     * @param calcFieldName 统计字段
     * @param <T1> 分组聚合分组字段的值类型
     * @param <T2> 分组聚合统计字段的值类型
     * @param <R> 分组聚合统计结果类型
     * @return 字段的聚合count数量 Map<字段值, 该字段值聚合结果>
     */
    public static <T1, T2, R> Map<T1, R> groupCalc(BaseModelMapper<?> mapper, HumanCondition condition,
                                                   String groupFieldName, String calcFieldName,
                                                   Function<List<T2>, R> calc) {
        condition.selectAs(groupFieldName, GroupCalcItemBO<T1, T2>::getTarget).groupBy(groupFieldName);
        condition.selectAs(calcFieldName, GroupCalcItemBO<T1, T2>::getCalcSource);
        var groupCalcItemBOs = mapper.list(condition, new GroupCalcItemBO<T1, T2>().getClass());
        Map<T1, List<T2>> groupValues = new HashMap<>();
        groupCalcItemBOs.forEach(groupCalcItem -> {
            List<T2> values = groupValues.getOrDefault((T1) groupCalcItem.getTarget(), new ArrayList<>());
            values.add((T2) groupCalcItem.getCalcSource());
            groupValues.put((T1) groupCalcItem.getTarget(), values);
        });
        Map<T1, R> groupCalcResult = new HashMap<>();
        groupValues.forEach((group, values) -> {
            groupCalcResult.put(group, calc.apply(values));
        });
        return groupCalcResult;
    }

    /**
     * 查询指定字段的聚合count数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param fieldNames 统计字段名称列表
     * @return 字段的聚合count数量 Map<字段名, 该字段值统计数量>
     */
    public static Map<String, Long> count(BaseModelMapper<?> mapper, HumanCondition condition,
                                          List<String> fieldNames) {
        for (var fieldName : fieldNames) {
            condition.selectAs(Count.f(fieldName).calcEmptyNull().distinct(), fieldName);
        }
        var countResultList = mapper.originList(condition);
        if (countResultList.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Long> countResult = new HashMap<>();
        countResultList.get(0).forEach((fieldName, countValue) -> {
            countResult.put(fieldName, Long.valueOf(countValue.toString()));
        });
        return countResult;
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
        for (var fieldName : fieldNames) {
            condition.selectAs(Sum.f(fieldName), fieldName);
        }
        var sumResultList = mapper.originList(condition);
        if (sumResultList.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, S> sumResult = new HashMap<>();
        sumResultList.get(0).forEach((fieldName, sumValue) -> {
            sumResult.put(fieldName, (S) sumValue);
        });
        return sumResult;
    }

    /**
     * 分组查询指定字段的聚合计算结果，对于无法直接聚合统计的内容
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param fieldCalcMap 统计字段对应的计算方法
     * @return 字段的聚合count数量 Map<字段值, 该字段值聚合结果>
     */
    public static Map<String, Object> calc(BaseModelMapper<?> mapper, HumanCondition condition,
                                           Map<String, Function<List<Object>, Object>> fieldCalcMap) {
        var fieldNames = fieldCalcMap.keySet();
        fieldNames.forEach(fieldName -> condition.selectAs(fieldName, fieldName));
        var result = mapper.originList(condition);
        Map<String, List<Object>> fieldValuesMap = new HashMap<>();
        result.forEach(resultItem -> {
            fieldNames.forEach(fieldName -> {
                var fieldValue = resultItem.get(fieldName);
                var fieldValues = fieldValuesMap.getOrDefault(fieldName, new ArrayList<>());
                fieldValues.add(fieldValue);
                fieldValuesMap.put(fieldName, fieldValues);
            });
        });
        Map<String, Object> fieldCalcValueMap = new HashMap<>();
        fieldValuesMap.forEach((fieldName, fieldValues) -> {
            fieldCalcValueMap.put(fieldName, fieldCalcMap.get(fieldName).apply(fieldValues));
        });
        return fieldCalcValueMap;
    }

}
