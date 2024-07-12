 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TaskActionEnum.java.
 // File size 676.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName TaskActionEnum
 * @Description 任务操作枚举类：1 start， 2 stop, 3 reset
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum TaskActionEnum {

    INIT("INIT"),

    START("START"),

    STOP("STOP"),

    RESET("RESET");

    private final String action;

    TaskActionEnum(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
