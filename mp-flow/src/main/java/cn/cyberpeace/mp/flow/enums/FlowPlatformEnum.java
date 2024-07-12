 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPlatformEnum.java.
 // File size 662.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
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
