package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.bo.GroupCountBO;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StatCommon {

    private StatCommon() {}

    /**
     * 分组查询指定字段的聚合统计数量
     * @param mapper 数据mapper对象
     * @param condition 已完成设置的HumanCondition查询条件（如关联查询需要已指向字段对应的表）
     * @param groupFieldName 分组聚合字段
     * @param <T> 分组聚合字段的值类型
     * @return 字段的聚合统计数量 Map<字段值, 该字段值统计数量>
     */
    public static <T> Map<T, Integer> groupCount(BaseModelMapper<?> mapper, HumanCondition condition,
                                                 String groupFieldName) {
        condition.selectAs(groupFieldName, GroupCountBO<T>::getTarget)
                .selectAs(Count.f(groupFieldName), GroupCountBO<T>::getCount)
                .groupBy(groupFieldName);
        var groupCountBOs = mapper.list(condition, new GroupCountBO<T>().getClass());
        return groupCountBOs.stream()
                .collect(Collectors.toMap(groupCountBO -> (T) groupCountBO.getTarget(), GroupCountBO::getCount));
    }

}
