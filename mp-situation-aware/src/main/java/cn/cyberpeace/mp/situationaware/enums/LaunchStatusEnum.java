 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LaunchStatusEnum.java.
 // File size 662.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * launch status
 */
public enum LaunchStatusEnum {

    ERROR("error"),

    SUCCESS("success");

    private String value;

    /**
     * LaunchStatusEnum
     *
     * @param value
     */
    LaunchStatusEnum(String value) {
        this.value = value;
    }

    /**
     * getValue
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * toValue
     *
     * @return int
     */
    @JsonValue
    public int toValue(){
       return this.ordinal();
    }
}
