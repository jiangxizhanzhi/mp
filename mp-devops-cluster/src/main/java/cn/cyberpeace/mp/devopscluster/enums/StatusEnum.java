 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name StatusEnum.java.
 // File size 686.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName StatusEnum
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum StatusEnum {

    NO,

    YES;

    /**
     * fromValue
     *
     * @param ordinal
     * @return
     */
    @JsonCreator
    public static StatusEnum fromValue(Integer ordinal) {
        return values()[ordinal];
    }

    /**
     * toValue
     *
     * @return
     */
    @JsonValue
    public Integer toValue() {
        return this.ordinal();
    }
}
