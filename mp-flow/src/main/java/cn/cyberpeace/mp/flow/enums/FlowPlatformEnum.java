package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum FlowPlatformEnum {

    COMMON("COMMON"),

    TREX("TREX");

    private final String platform;

    FlowPlatformEnum(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return platform;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
