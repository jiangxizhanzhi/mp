package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 流量发生平台枚举类：0 通用，1 Trex流量发生器
 */
public enum FlowPlatformEnum {

    COMMON("COMMON"),

    TREX("TREX");

    private final String group;

    FlowPlatformEnum(String group) {
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
