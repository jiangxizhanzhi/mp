package cn.cyberpeace.mp.collection.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/14 19:51
 */
@Slf4j
public class AgentNameUtils {

    /**
     * 剔除null
     * @param agentNames
     * @return
     */
    public static List<String> agentNamesExcludeNullValue(List<String> agentNames) {
        return agentNames.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static boolean isAgentNamesEmpty(List<String> agentNames) {
        List<String> processedAgentNames = agentNamesExcludeNullValue(agentNames);
        boolean isEmpty = CollectionUtils.isEmpty(processedAgentNames);
        if (isEmpty) {
            log.warn("agentNames is empty:{}", agentNames);
        }
        return isEmpty;
    }

}
