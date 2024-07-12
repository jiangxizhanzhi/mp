 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InteractiveTypeEnum.java.
 // File size 726.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName InteractiveTypeEnum
 * @Description 交互类型枚举类：0 背景流量，1 攻击流量，2 全流量
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum InteractiveTypeEnum {

    BACKGROUND("BACKGROUND"),

    ATTACK("ATTACK"),

    FULL("FULL");

    private final String interactive;

    InteractiveTypeEnum(String interactive) {
        this.interactive = interactive;
    }

    @Override
    public String toString() {
        return interactive;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
