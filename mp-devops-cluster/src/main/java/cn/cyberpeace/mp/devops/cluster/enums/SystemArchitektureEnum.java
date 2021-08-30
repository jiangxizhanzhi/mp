package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统架构
 */
public enum SystemArchitektureEnum {

    THIRTY_TWO("32"),

    SIXTY_FOUR("64"),

    OTHER("other");

    private String desc;

    SystemArchitektureEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    private static Map<String, SystemArchitektureEnum> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(SystemArchitektureEnum.values()).forEach(
                t -> map.put(t.getDesc(), t)
        );
    }

    @JsonCreator
    public static SystemArchitektureEnum fromValue(String desc) {
        return map.get(desc);
    }

    @JsonValue
    public String toValue() {
        return this.getDesc();
    }

}
