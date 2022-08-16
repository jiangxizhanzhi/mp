package cn.cyberpeace.mp.situationaware.constant;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 态势感知全局缓存
 */
public class SituationalAwareConstant {
    public static LoadingCache<String, String> wazuhToken;

    public static Integer ES_DOC_MAXCOUNT = 10000;

    public static String ERROR_CODE_PREFIX = "situationaware.error_code.";

    public static List<String> VULNERABILITY_GROUP = List.of("vulnerability-detector");

    public static List<String> FILE_MODIFY_GROUP = List.of("syscheck_file");

    public static List<String> REGISTRY_MODIFY_GROUP = List.of("syscheck_registry");
}
