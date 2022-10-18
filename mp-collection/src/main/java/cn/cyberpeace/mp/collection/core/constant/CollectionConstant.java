package cn.cyberpeace.mp.collection.core.constant;

import java.util.List;

/**
 * 数据采集静态变量
 */
public class CollectionConstant {
    private CollectionConstant() {

    }

    public static final List<String> VULNERABILITY_GROUP = List.of("vulnerability-detector");

    public static final List<String> FILE_MODIFY_GROUP = List.of("syscheck_file");

    public static final List<String> REGISTRY_MODIFY_GROUP = List.of("syscheck_registry");

    public static final Integer FILE = 0;

    public static final Integer REGISTRY = 1;
}
