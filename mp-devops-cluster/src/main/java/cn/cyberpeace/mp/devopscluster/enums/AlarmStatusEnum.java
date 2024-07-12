 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AlarmStatusEnum.java.
 // File size 794.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName AlarmStatusEnum
 * @Description 告警状态：0 生效， 1失效
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum AlarmStatusEnum {

    ENABLED("ENABLED"),

    DISABLED("DISABLED");

    private String desc;

    /**
     * AlarmStatusEnum
     *
     * @param desc
     */
    AlarmStatusEnum(String desc) {
        this.desc = desc;
    }

    /**
     * getDesc
     *
     * @return
     */
    public String getDesc() {
        return desc;
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
