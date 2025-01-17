 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SiteTypeEnums.java.
 // File size 457.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 站点枚举
 * @author wanggang
 */
public enum SiteTypeEnums {

    //MP中台
    MP;

    @JsonCreator
    public static SiteTypeEnums fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
