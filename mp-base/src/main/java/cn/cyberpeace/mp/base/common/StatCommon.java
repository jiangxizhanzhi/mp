package cn.cyberpeace.mp.base.common;

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
     * @param fieldCalcMap 统计字段对应的计算方法
     * @param <T> 分组聚合分组字段的值类型
     * @return 字段的聚合count数量 Map<字段, Map<分组字段值, 该字段值聚合结果>>
     */
    public static <T> Map<String, Map<T, Object>> groupCalc(
            BaseModelMapper<?> mapper, HumanCondition condition, String groupFieldName,
            Map<String, Function<List<Object>, Object>> fieldCalcMap) {
        condition.selectAs(groupFieldName, groupFieldName).groupBy(groupFieldName);
        var fieldNames = fieldCalcMap.keySet();
        fieldNames.forEach(fieldName -> condition.selectAs(fieldName, fieldName));

        var result = mapper.originList(condition);
        Map<String, Map<T, List<Object>>> fieldGroupValuesMap = new HashMap<>();
        result.forEach(resultItem -> {
            fieldNames.forEach(fieldName -> {
                var fieldGroupValues = fieldGroupValuesMap.getOrDefault(fieldName, new HashMap<>());
                var groupValue = (T) resultItem.get(groupFieldName);
                var groupValues = fieldGroupValues.getOrDefault(groupValue, new ArrayList<>());
                var fieldValue = resultItem.get(fieldName);
                groupValues.add(fieldValue);
                fieldGroupValues.put(groupValue, groupValues);
                fieldGroupValuesMap.put(fieldName, fieldGroupValues);
            });
        });

        Map<String, Map<T, Object>> fieldGroupCalcValueMap = new HashMap<>();
        fieldGroupValuesMap.forEach((fieldName, fieldGroupValues) -> {
            Map<T, Object> fieldGroupCalcValue = new HashMap<>();
            fieldGroupCalcValueMap.put(fieldName, fieldGroupCalcValue);
            var calc = fieldCalcMap.get(fieldName);
            fieldGroupValues.forEach((groupValue, groupValues) -> {
                fieldGroupCalcValue.put(groupValue, calc.apply(groupValues));
            });
        });
        return fieldGroupCalcValueMap;
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
