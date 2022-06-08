package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 网口类型：0 背景流量，1攻击流量
 */
public enum PortTypeEnum {

    BACKGROUND("BACKGROUND"),

    ATTACK("ATTACK");

    private final String resource;

    PortTypeEnum(String resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return resource;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
