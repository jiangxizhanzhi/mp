package cn.cyberpeace.mp.collectionapi.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.collectionapi.config.GroupConfig;
import org.apache.commons.collections4.CollectionUtils;

import java.util.stream.Collectors;

public class GroupUtil {
    private GroupUtil() {

    }

    private static GroupConfig groupConfig;

    public static void updateGroupConfig(GroupConfig groupConfig) {
        GroupUtil.groupConfig = groupConfig;
    }

    public static GroupConfig.Group getGroup(String groupId) {
        if (groupConfig == null) {
            groupConfig = BeanCommon.getBean(GroupConfig.class);
        }
        var groups = groupConfig.getGroups().stream()
                .filter(g -> g.getGroupId().equals(groupId))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(groups)) {
            return groups.get(0);
        }
        return null;
    }
}
