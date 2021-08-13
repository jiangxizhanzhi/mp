package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 资源类型枚举类：0 pcap包，1 脚本
 */
public enum ResourceTypeEnum {

    PACP("PACP"),

    SCRIPT("SCRIPT");

    private final String group;

    ResourceTypeEnum(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return group;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
