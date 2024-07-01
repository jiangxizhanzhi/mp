package cn.cyberpeace.mp.situationaware.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
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
     * agentNamesExcludeNullValue
     *
     * @param agentNames
     * @return
     */
    public List<String> agentNamesExcludeNullValue(List<String> agentNames) {
        List<String> nullExcludedAgentNames = new ArrayList<>();
        for (String agentName : agentNames) {
            if (Objects.nonNull(agentName)) {
                nullExcludedAgentNames.add(agentName);
            }
        }
        return nullExcludedAgentNames;
    }

    /**
     * isAgentNamesEmpty
     *
     * @param agentNames
     * @return boolean
     */
    public boolean isAgentNamesEmpty(List<String> agentNames) {
        List processedAgentNames = agentNamesExcludeNullValue(agentNames);
        boolean isEmpty = CollectionUtils.isEmpty(processedAgentNames);
        if(isEmpty) {
            log.warn("agentNames is empty:{}", agentNames);
        }
        return isEmpty;
    }

}
