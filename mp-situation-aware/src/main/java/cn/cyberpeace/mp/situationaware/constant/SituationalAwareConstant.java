package cn.cyberpeace.mp.situationaware.constant;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 态势感知全局缓存
 */
public class SituationalAwareConstant {
    /**
     * 分组id url 分组
     */
    public static LoadingCache<String, String> groupIdSessionUrlCache;

    /**
     * 分组id
     */
    public static LoadingCache<String, String> wazuhToken;

    /**
     * openstack network查询
     */
    public static String ArkimeNetworkId;

    public static Integer ES_DOC_MAXCOUNT = 10000;

    public static String ERROR_CODE_PREFIX = "situationaware.error_code.";

    public static String PROBE_TYPES_CODE_PREFIX = "situationaware.probe.type.";

    public static String PROBE_TYPE_PREFIX = "situationaware.probe.type";
    public static String OS_TYPE_PREFIX = "situationaware.os.type";

    public static List<String> monitorRuleIds = Lists.newArrayList("5715", "5502");
}
