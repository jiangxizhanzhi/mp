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
