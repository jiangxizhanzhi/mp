package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum AccessTypeEnum {

    REMOTE_ADDRESS("remote_address"),

    DEVICE_PORT("device_port"),

    WIRELESS("wireless"),

    MAC_ADDRESS("macaddress");

    private String desc;

    AccessTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    private static Map<String, AccessTypeEnum> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(AccessTypeEnum.values()).forEach(
                t -> map.put(t.getDesc(), t)
        );
    }

    @JsonCreator
    public static AccessTypeEnum fromValue(String desc) {
        return map.get(desc);
    }

    @JsonValue
    public String toValue() {
        return this.getDesc();
    }
}
