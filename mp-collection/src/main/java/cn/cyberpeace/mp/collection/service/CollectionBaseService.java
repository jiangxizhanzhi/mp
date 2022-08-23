package cn.cyberpeace.mp.collection.service;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/14 19:51
 */
@Slf4j
public class CollectionBaseService {

    /**
     * 剔除null
     * @param agentNames
     * @return
     */
    public List<String> agentNamesExcludeNullValue(List<String> agentNames) {
        List<String> nullExcludedAgentNames = agentNames.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return nullExcludedAgentNames;
    }

    public boolean isAgentNamesEmpty(List<String> agentNames) {
        List processedAgentNames = agentNamesExcludeNullValue(agentNames);
        boolean isEmpty = CollectionUtil.isEmpty(processedAgentNames);
        if(isEmpty) {
            log.warn("agentNames is empty:{}", agentNames);
        }
        return isEmpty;
    }

}
