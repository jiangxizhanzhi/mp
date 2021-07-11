package cn.cyberpeace.mp.cscene.constant;

import cn.hutool.core.collection.CollUtil;

import java.util.List;


public final class CsceneBizConstant {
    public static class Event {
        public static final int UNUSE = -1;
    }

    public class EventStatus {
        public static final int UNUSE = -1;
    }

    
    public static List<String> OS_CATEGORY = CollUtil.toList("020101", "020102", "020103", "020104", "020105",
            "020106", "020107", "020108");

    
    public static List<String> APPLICATION_CATEGORY = CollUtil.toList("020201", "020202", "020203", "020204",
            "020205", "020206", "020207", "020208", "020209", "020210", "020211");

    
    public static List<String> SECURITY_CATEGORY = CollUtil.toList("020301", "020302", "020303", "020304", "020305",
            "020306", "020307", "020308", "020309", "020310", "020311", "020312");

    public static List<String> FILE_MODIFY_GROUP = CollUtil.toList("syscheck_file");

    public static List<String> REGISTRY_MODIFY_GROUP = CollUtil.toList("syscheck_registry");

    public static List<String> VULNERABILITY_GROUP = CollUtil.toList("vulnerability-detector");
}
