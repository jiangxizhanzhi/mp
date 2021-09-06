package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PortConfigEnum {

    MANAGE_PORT("管理口"),

    WAN_PORT("Wan口"),

    LAN_PORT("Lan口");

    private String desc;

    PortConfigEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    private static Map<Integer, PortConfigEnum> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(PortConfigEnum.values()).forEach(
                t -> map.put(t.ordinal(), t)
        );
    }

    @JsonCreator
    public static PortConfigEnum fromValue(String desc) {
        return map.get(desc);
    }

    @JsonValue
    public Integer toValue() {
        return this.ordinal();
    }
}
