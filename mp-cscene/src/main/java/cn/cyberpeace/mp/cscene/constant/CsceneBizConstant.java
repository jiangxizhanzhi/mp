package cn.cyberpeace.mp.cscene.constant;

import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/1/6
 */
public final class CsceneBizConstant {
    public static class Event {
        public static final int UNUSE = -1;
    }

    public class EventStatus {
        public static final int UNUSE = -1;
    }

    public static List<String> OS_CATEGORY = new ArrayList<>(CollectionUtil.toList("020101", "020102", "020103",
            "020104", "020105", "020106"));

    public static List<String> APPLICATION_CATEGORY = new ArrayList<>(CollectionUtil.toList("020201", "020202",
            "020203", "020204", "020205", "020206", "020207", "020208"));

    public static List<String> SECURITY_CATEGORY = new ArrayList<>(CollectionUtil.toList("020301", "020302",
            "020303", "020304", "020305"));
}
