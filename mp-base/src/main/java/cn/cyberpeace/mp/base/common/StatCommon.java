package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.bo.GroupCountBO;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Sum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public static <T> Map<T, Integer> groupCount(BaseModelMapper<?> mapper, HumanCondition condition,
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
    public static <T> Map<T, Integer> groupCount(BaseModelMapper<?> mapper, HumanCondition condition,
                                                 String groupFieldName, String countFieldName, boolean distinct) {
        condition.selectAs(groupFieldName, GroupCountBO<T>::getTarget).groupBy(groupFieldName);
        if (distinct) {
            condition.selectAs(Count.f(countFieldName).distinct(), GroupCountBO<T>::getCount);
        } else {
            condition.selectAs(Count.f(countFieldName), GroupCountBO<T>::getCount);
        }
        var groupCountBOs = mapper.list(condition, new GroupCountBO<T>().getClass());
        return groupCountBOs.stream()
                .collect(Collectors.toMap(groupCountBO -> (T) groupCountBO.getTarget(), GroupCountBO::getCount));
    }

    /**
     * 分组查询指定字段的聚合count数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param groupFieldName 分组聚合字段
     * @param sumFieldName 统计字段
     * @param <T> 分组聚合字段的值类型
     * @return 字段的聚合count数量 Map<字段值, 该字段值统计数量>
     */
    public static <T> Map<T, Integer> groupSum(BaseModelMapper<?> mapper, HumanCondition condition,
                                               String groupFieldName, String sumFieldName) {
        condition.selectAs(groupFieldName, GroupCountBO<T>::getTarget).groupBy(groupFieldName);
        condition.selectAs(Sum.f(sumFieldName), GroupCountBO<T>::getCount);
        var groupCountBOs = mapper.list(condition, new GroupCountBO<T>().getClass());
        return groupCountBOs.stream()
                .collect(Collectors.toMap(groupCountBO -> (T) groupCountBO.getTarget(), GroupCountBO::getCount));
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
            countResult.put(fieldName, (long) countValue);
        });
        return countResult;
    }

    /**
     * 查询指定字段的聚合sum数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param fieldNames 统计字段名称列表
     * @return 字段的聚合sum数量 Map<字段名, 该字段值统计数量>
     */
    public static Map<String, Long> sum(BaseModelMapper<?> mapper, HumanCondition condition,
                                        List<String> fieldNames) {
        for (var fieldName : fieldNames) {
            condition.selectAs(Sum.f(fieldName), fieldName);
        }
        var sumResultList = mapper.originList(condition);
        if (sumResultList.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Long> sumResult = new HashMap<>();
        sumResultList.get(0).forEach((fieldName, countValue) -> {
            sumResult.put(fieldName, (long) countValue);
        });
        return sumResult;
    }

}
