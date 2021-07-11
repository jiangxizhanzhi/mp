package cn.cyberpeace.mp.situationaware.constant;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.util.List;


public class SituationalAwareConstant {
    
    public static LoadingCache<String, String> groupIdSessionUrlCache;

    
    public static LoadingCache<String, String> wazuhToken;

    
    public static String ArkimeNetworkId;

    public static String ERROR_CODE_PREFIX = "situationaware.error_code.";

    public static List<String> monitorRuleIds = Lists.newArrayList("5715", "5502");

    
    public static final String MP_COMMON_OPTION_TYPE_SITUATION_OS_TYPE = "os_type";
    
    public static final String MP_COMMON_OPTION_TYPE_SITUATION_PROBE_TYPE = "probe_type";

    public static final String ES_TIMESTAMP_FIELD = "timestamp";
}
