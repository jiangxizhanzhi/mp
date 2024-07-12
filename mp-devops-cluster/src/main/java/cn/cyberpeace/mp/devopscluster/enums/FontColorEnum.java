 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FontColorEnum.java.
 // File size 766.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName FontColorEnum
 * @Description 字体颜色枚举信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum FontColorEnum {

    RED("红色"),

    GREEN("绿色");

    private String desc;

    /**
     * FontColorEnum
     *
     * @param desc
     */
    FontColorEnum(String desc) {
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
