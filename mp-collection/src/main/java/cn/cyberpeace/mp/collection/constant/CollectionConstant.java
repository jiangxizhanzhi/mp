package cn.cyberpeace.mp.collection.constant;

import com.google.common.cache.LoadingCache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 态势感知全局缓存
 */
public class CollectionConstant {
    public static LoadingCache<String, String> wazuhToken;

    public static String ERROR_CODE_PREFIX = "collection.error_code.";

    public static List<String> VULNERABILITY_GROUP = List.of("vulnerability-detector");

    public static List<String> FILE_MODIFY_GROUP = List.of("syscheck_file");

    public static List<String> REGISTRY_MODIFY_GROUP = List.of("syscheck_registry");

    public static final Map<String, String> wazuhUrl2Token = new ConcurrentHashMap<>();
}
