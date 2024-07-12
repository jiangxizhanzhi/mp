 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CustomFlagEnum.java.
 // File size 610.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName CustomFlagEnum
 * @Description 是否自定义：0 否， 1 是
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum CustomFlagEnum {

    NO("NO"),

    YES("YES");

    private final String custom;

    CustomFlagEnum(String resource) {
        this.custom = resource;
    }

    @Override
    public String toString() {
        return custom;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
