package cn.cyberpeace.mp.collection.constant;

import java.util.List;

/**
 * 数据采集静态变量
 */
public class CollectionConstant {
    public static String ERROR_CODE_PREFIX = "collection.error_code.";

    public static List<String> VULNERABILITY_GROUP = List.of("vulnerability-detector");

    public static List<String> FILE_MODIFY_GROUP = List.of("syscheck_file");

    public static List<String> REGISTRY_MODIFY_GROUP = List.of("syscheck_registry");
}
