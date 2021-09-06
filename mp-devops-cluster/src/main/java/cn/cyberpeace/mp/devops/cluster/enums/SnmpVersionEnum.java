package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum SnmpVersionEnum {

    V1,

    V2,

    V3;

    private static Map<String, SnmpVersionEnum> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(SnmpVersionEnum.values()).forEach(
                t -> map.put(t.name(), t)
        );
    }

    @JsonCreator
    public static SnmpVersionEnum fromValue(String name) {
        return map.get(name);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

}
