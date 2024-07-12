 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResourceTypeEnum.java.
 // File size 644.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName ResourceTypeEnum
 * @Description 资源类型枚举类：0 pcap包，1 脚本
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum ResourceTypeEnum {

    PACP("PACP"),

    SCRIPT("SCRIPT");

    private final String resource;

    ResourceTypeEnum(String resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return resource;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
