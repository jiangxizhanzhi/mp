package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName FlowPlatformEnum
 * @Description 流量发生平台枚举类：0 通用，1 Trex流量发生器
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
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
